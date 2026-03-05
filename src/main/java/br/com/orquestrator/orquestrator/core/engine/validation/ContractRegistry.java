package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataContract;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContractRegistry {

    private final Map<String, CompiledContract> cache = new ConcurrentHashMap<>(1024);
    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public void register(DataContract contract) {
        JsonSchema compiledSchema = null;
        if (contract.schemaDefinition() != null) {
            // COMPILAÇÃO NO BUILD-TIME/WARMUP: O custo pesado acontece aqui, apenas uma vez.
            compiledSchema = schemaFactory.getSchema(contract.schemaDefinition());
        }
        cache.put(contract.contextKey(), new CompiledContract(contract, compiledSchema));
    }

    public Optional<CompiledContract> get(String contextKey) {
        return Optional.ofNullable(cache.get(contextKey));
    }

    public record CompiledContract(DataContract definition, JsonSchema schema) {}
}
