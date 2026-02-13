package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
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
    // Cache de esquemas compilados para evitar parsing repetitivo
    private final Map<String, JsonSchema> compiledSchemaCache = new ConcurrentHashMap<>();

    public SchemaValidatorInterceptor(ObjectMapper objectMapper) {
        super(SchemaValidatorConfig.class);
        this.objectMapper = objectMapper;
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, SchemaValidatorConfig config, TaskDefinition taskDef) {
        next.proceed(data);

        // Fail Fast: Se não houver schema ou se a amostragem decidir pular
        if (taskDef.getResponseSchema() == null || shouldSkipValidation(config)) {
            return;
        }

        try {
            JsonNode outputJson = buildOutputJson(data, taskDef);
            if (outputJson != null) {
                validateWithCache(outputJson, taskDef, config);
            }
        } catch (Exception e) {
            log.warn("Erro interno na validação de schema para task {}: {}", taskDef.getNodeId(), e.getMessage());
        }
    }

    private boolean shouldSkipValidation(SchemaValidatorConfig config) {
        if (config == null || config.sampleRate() >= 100) return false;
        return ThreadLocalRandom.current().nextInt(100) >= config.sampleRate();
    }

    private JsonNode buildOutputJson(TaskData data, TaskDefinition taskDef) {
        if (taskDef.getProduces() == null || taskDef.getProduces().isEmpty()) return null;

        // Java 21: Stream API mais limpa para construir o mapa de saída
        Map<String, Object> outputMap = taskDef.getProduces().stream()
                .filter(spec -> data.get(spec.name()) != null)
                .collect(Collectors.toMap(DataSpec::name, spec -> data.get(spec.name())));

        return objectMapper.valueToTree(outputMap);
    }

    private void validateWithCache(JsonNode data, TaskDefinition taskDef, SchemaValidatorConfig config) {
        String nodeId = taskDef.getNodeId().value();

        // Recupera ou compila o schema (operação atômica no cache)
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
            // Lançamos uma exceção de negócio para o motor tratar
            throw new RuntimeException(message);
        }
    }
}