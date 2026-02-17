package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.SchemaValidatorConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Component("SCHEMA_VALIDATOR")
public class SchemaValidatorInterceptor extends TypedTaskInterceptor<SchemaValidatorConfig> {

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;
    private final Map<String, JsonSchema> compiledSchemaCache = new ConcurrentHashMap<>();

    public SchemaValidatorInterceptor(ObjectMapper objectMapper) {
        super(SchemaValidatorConfig.class);
        this.objectMapper = objectMapper;
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, SchemaValidatorConfig config, TaskDefinition taskDef) {
        Object result = next.proceed(context);

        if (taskDef.getResponseSchema() == null || shouldSkipValidation(config)) {
            return result;
        }

        try {
            JsonNode outputJson = buildOutputJson(context, taskDef);
            if (outputJson != null) {
                validateWithCache(outputJson, taskDef, config);
            }
        } catch (Exception e) {
            log.warn("Erro interno na validação de schema para task {}: {}", taskDef.getNodeId(), e.getMessage());
        }
        return result;
    }

    private boolean shouldSkipValidation(SchemaValidatorConfig config) {
        if (config == null || config.sampleRate() >= 100) return false;
        return ThreadLocalRandom.current().nextInt(100) >= config.sampleRate();
    }

    private JsonNode buildOutputJson(ExecutionContext context, TaskDefinition taskDef) {
        if (taskDef.getProduces() == null || taskDef.getProduces().isEmpty()) return null;

        Map<String, Object> outputMap = taskDef.getProduces().stream()
                .filter(spec -> context.get(spec.name()) != null)
                .collect(Collectors.toMap(DataSpec::name, spec -> context.get(spec.name())));

        return objectMapper.valueToTree(outputMap);
    }

    private void validateWithCache(JsonNode data, TaskDefinition taskDef, SchemaValidatorConfig config) {
        String nodeId = taskDef.getNodeId().value();

        JsonSchema schema = compiledSchemaCache.computeIfAbsent(
                STR."\{nodeId}_\{taskDef.getVersion()}",
                _ -> schemaFactory.getSchema(taskDef.getResponseSchema())
        );

        Set<ValidationMessage> errors = schema.validate(data);

        if (!errors.isEmpty()) {
            handleValidationError(nodeId, errors, config);
        }
    }

    private void handleValidationError(String nodeId, Set<ValidationMessage> errors, SchemaValidatorConfig config) {
        String message = STR."Schema Violation na task \{nodeId}: \{errors}";
        log.warn(message);

        if (config != null && config.failOnInvalid()) {
            throw new RuntimeException(message);
        }
    }
}
