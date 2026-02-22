package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.SchemaValidatorConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
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

@Slf4j
@Component("SCHEMA_VALIDATOR_INTERCEPTOR")
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
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, SchemaValidatorConfig config, String nodeId) {
        TaskResult result = next.proceed(context);

        // Nota: O schema agora deve vir na config ou ser buscado de outra forma, 
        // já que removemos a TaskDefinition da assinatura.
        if (shouldSkipValidation(config)) {
            return result;
        }

        try {
            JsonNode outputJson = buildOutputJson(result.body());
            if (outputJson != null) {
                validateWithCache(outputJson, nodeId, config);
            }
        } catch (Exception e) {
            log.warn("Erro interno na validação de schema para task {}: {}", nodeId, e.getMessage());
        }
        return result;
    }

    private boolean shouldSkipValidation(SchemaValidatorConfig config) {
        if (config == null || config.sampleRate() >= 100) return false;
        return ThreadLocalRandom.current().nextInt(100) >= config.sampleRate();
    }

    private JsonNode buildOutputJson(Object body) {
        if (body == null) return null;
        if (body instanceof JsonNode node) return node;
        return objectMapper.valueToTree(body);
    }

    private void validateWithCache(JsonNode data, String nodeId, SchemaValidatorConfig config) {
        // Lógica de cache simplificada sem a versão da task por enquanto
        JsonSchema schema = compiledSchemaCache.computeIfAbsent(
                nodeId,
                _ -> {
                    // Aqui precisaríamos do schema vindo da config
                    return null; 
                }
        );

        if (schema == null) return;

        Set<ValidationMessage> errors = schema.validate(data);
        if (!errors.isEmpty()) {
            handleValidationError(nodeId, errors, config);
        }
    }

    private void handleValidationError(String nodeId, Set<ValidationMessage> errors, SchemaValidatorConfig config) {
        String message = "Schema Violation na task " + nodeId + ": " + errors;
        log.warn(message);
        if (config != null && config.failOnInvalid()) {
            throw new RuntimeException(message);
        }
    }
}
