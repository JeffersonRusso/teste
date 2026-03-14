package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.core.ports.output.DataContractProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataContractFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ContractRegistry: Registro centralizado de contratos.
 * 100% Hexagonal: Desacoplado de implementações técnicas via Portas de Saída.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractRegistry {

    private final DataContractProvider contractProvider;
    private final DataContractFactory contractFactory; // Injeção via Porta
    private final Map<String, DataContract> cache = new ConcurrentHashMap<>();

    /**
     * Recupera um contrato compilado.
     */
    public Optional<DataContract> get(String key) {
        return Optional.ofNullable(cache.computeIfAbsent(key, this::loadAndCompile));
    }

    private DataContract loadAndCompile(String key) {
        return contractProvider.findByKey(key)
                .map(contract -> {
                    log.info("Compilando contrato para a chave: {}", key);
                    // Core chama a porta de fábrica
                    return contractFactory.create(contract.key(), contract.schemaDefinition());
                })
                .orElse(null);
    }
}
