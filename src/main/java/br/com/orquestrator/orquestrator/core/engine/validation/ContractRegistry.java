package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.DataContractRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.DataContractEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContractRegistry {

    private final DataContractRepository repository;
    private final ObjectMapper objectMapper;
    private final Map<String, CompiledContract> cache = new ConcurrentHashMap<>();

    public record CompiledContract(
        String key,
        JsonNode schema,
        boolean isRequired
    ) {}

    public Optional<CompiledContract> get(String key) {
        return Optional.ofNullable(cache.computeIfAbsent(key, this::compile));
    }

    private CompiledContract compile(String key) {
        return repository.findById(key)
                .map(this::toCompiled)
                .orElse(null);
    }

    private CompiledContract toCompiled(DataContractEntity entity) {
        try {
            JsonNode schema = entity.getSchemaDefinition() != null 
                ? objectMapper.readTree(entity.getSchemaDefinition()) 
                : null;
            
            return new CompiledContract(
                entity.getContextKey(),
                schema,
                entity.getIsRequired()
            );
        } catch (Exception e) {
            throw new RuntimeException("Falha ao compilar contrato de dados: " + entity.getContextKey(), e);
        }
    }
}
