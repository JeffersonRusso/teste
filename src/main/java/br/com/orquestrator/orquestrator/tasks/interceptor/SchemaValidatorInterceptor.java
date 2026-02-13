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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component("SCHEMA_VALIDATOR")
public class SchemaValidatorInterceptor extends TypedTaskInterceptor<SchemaValidatorConfig> {

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;

    public SchemaValidatorInterceptor(ObjectMapper objectMapper) {
        super(SchemaValidatorConfig.class);
        this.objectMapper = objectMapper;
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, SchemaValidatorConfig config, TaskDefinition taskDef) {
        next.proceed(data);

        if (taskDef.getResponseSchema() == null || shouldSkipValidation(config)) {
            return;
        }

        try {
            JsonNode outputJson = buildOutputJson(data, taskDef);
            if (outputJson != null) {
                validate(outputJson, taskDef.getResponseSchema(), taskDef.getNodeId().value(), config);
            }
        } catch (Exception e) {
            log.warn("Erro interno na validação de schema: {}", e.getMessage());
        }
    }

    private boolean shouldSkipValidation(SchemaValidatorConfig config) {
        if (config == null) return false;
        return ThreadLocalRandom.current().nextInt(100) >= config.sampleRate();
    }

    private JsonNode buildOutputJson(TaskData data, TaskDefinition taskDef) {
        List<DataSpec> produces = taskDef.getProduces();
        if (produces == null || produces.isEmpty()) return null;

        Map<String, Object> outputMap = new HashMap<>();
        for (int i = 0; i < produces.size(); i++) {
            String name = produces.get(i).name();
            Object value = data.get(name);
            if (value != null) {
                outputMap.put(name, value);
            }
        }
        
        return objectMapper.valueToTree(outputMap);
    }

    private void validate(JsonNode data, JsonNode schemaNode, String nodeId, SchemaValidatorConfig config) {
        JsonSchema schema = schemaFactory.getSchema(schemaNode);
        Set<ValidationMessage> errors = schema.validate(data);

        if (!errors.isEmpty()) {
            String message = "Schema Violation na task " + nodeId + ": " + errors;
            log.warn(message);
            
            if (config != null && config.failOnInvalid()) {
                throw new RuntimeException(message);
            }
        }
    }
}
