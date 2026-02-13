package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigProvider;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FlowConfigEntity;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Adaptador JPA para fornecimento de configurações de fluxo.
 * Realiza a tradução entre entidades de banco de dados e o modelo de domínio.
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class JpaFlowConfigAdapter implements FlowConfigProvider {

    private final FlowConfigRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "flow_configs", key = "#operationType", unless = "#result == null")
    public Optional<FlowDefinition> getFlow(String operationType) {
        log.debug("Cache MISS: Buscando FlowConfig mais recente para [{}]", operationType);
        return repository.findLatestActive(operationType)
                .map(this::mapToDefinition);
    }

    @Override
    @Cacheable(value = "flow_configs_version", key = "#operationType + '-' + #version", unless = "#result == null")
    public Optional<FlowDefinition> getFlow(String operationType, Integer version) {
        if (version == null) {
            return getFlow(operationType);
        }
        log.debug("Cache MISS: Buscando FlowConfig para [{}] versão [{}]", operationType, version);
        return repository.findSpecificVersion(operationType, version)
                .map(this::mapToDefinition);
    }

    private FlowDefinition mapToDefinition(FlowConfigEntity entity) {
        return new FlowDefinition(
                entity.getOperationType(),
                parseJson(entity.getRequiredOutputs(), new TypeReference<Set<String>>() {}),
                parseJson(entity.getAllowedTasks(), new TypeReference<Set<TaskReference>>() {})
        );
    }

    /**
     * Método genérico para converter JsonNode em coleções tipadas.
     */
    private <T> Set<T> parseJson(JsonNode json, TypeReference<Set<T>> typeReference) {
        if (json == null || !json.isArray()) {
            return Collections.emptySet();
        }
        try {
            // O Jackson resolve automaticamente a conversão de JsonNode para o Set desejado
            Set<T> result = objectMapper.convertValue(json, typeReference);
            return result != null ? Collections.unmodifiableSet(result) : Collections.emptySet();
        } catch (Exception e) {
            log.error("Falha ao parsear configuração JSON: {} [Tipo: {}]", e.getMessage(), typeReference.getType());
            return Collections.emptySet();
        }
    }
}
