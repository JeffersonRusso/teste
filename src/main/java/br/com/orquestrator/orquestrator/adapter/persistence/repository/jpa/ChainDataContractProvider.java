package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter.DataContractMapper;
import br.com.orquestrator.orquestrator.core.ports.output.DataContractProvider;
import br.com.orquestrator.orquestrator.domain.rules.DataContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChainDataContractProvider: Implementa a busca de contratos seguindo uma ordem de prioridade.
 * 
 * Este provider está no mesmo pacote que o DataContractRepository (package-private),
 * permitindo a injeção direta sem expor o repositório para o resto do sistema.
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class ChainDataContractProvider implements DataContractProvider {

    // Injeção de repositório package-private permitida por estar no mesmo pacote
    private final DataContractRepository repository;
    private final ResourceLoader resourceLoader;

    @Override
    public Optional<DataContract> findByKey(String key) {
        log.debug("Buscando contrato: {}", key);

        // 1. Tenta no Banco de Dados através do repositório JPA
        Optional<DataContract> dbContract = repository.findById(key)
                .map(DataContractMapper::toDomain);

        if (dbContract.isPresent()) {
            return dbContract;
        }

        // 2. Se não encontrar, tenta carregar do arquivo local
        return loadFromFile(key);
    }

    private Optional<DataContract> loadFromFile(String key) {
        try {
            Resource resource = resourceLoader.getResource("classpath:contracts/" + key + ".json");
            if (!resource.exists()) {
                log.warn("Contrato [{}] não encontrado no Banco nem em Arquivo.", key);
                return Optional.empty();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String schema = reader.lines().collect(Collectors.joining("\n"));
                return Optional.of(new DataContract(key, schema, "Carregado do arquivo local"));
            }
        } catch (Exception e) {
            log.error("Erro ao carregar contrato [{}] do arquivo: {}", key, e.getMessage());
            return Optional.empty();
        }
    }
}
