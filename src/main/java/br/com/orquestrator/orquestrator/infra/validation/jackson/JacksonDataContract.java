package br.com.orquestrator.orquestrator.infra.validation.jackson;

import br.com.orquestrator.orquestrator.core.engine.validation.DataContract;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * JacksonDataContract: Implementação de infraestrutura para validação de JSON Schema.
 * Esta classe é o ADAPTER que esconde as bibliotecas externas do Core.
 */
@Slf4j
public class JacksonDataContract implements DataContract {

    private final String contextKey;
    private final JsonSchema schema;
    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonDataContract(String key, String schemaDefinition) {
        this.contextKey = key;
        this.schema = compileSchema(schemaDefinition);
    }

    private JsonSchema compileSchema(String schemaDefinition) {
        try {
            if (schemaDefinition == null || schemaDefinition.isBlank()) return null;
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            return factory.getSchema(mapper.readTree(schemaDefinition));
        } catch (Exception e) {
            log.error("Falha técnica ao compilar schema para [{}]: {}", contextKey, e.getMessage());
            return null;
        }
    }

    @Override
    public void validate(DataNode data) {
        if (schema == null || data == null || data.isMissing()) return;

        // Converte DataNode -> JsonNode internamente para o validador
        JsonNode jsonValue = mapper.valueToTree(data.asNative());

        Set<ValidationMessage> errors = schema.validate(jsonValue);
        if (!errors.isEmpty()) {
            String message = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("; "));
            
            throw new IllegalArgumentException("Violação de contrato para '" + contextKey + "': " + message);
        }
    }
}
