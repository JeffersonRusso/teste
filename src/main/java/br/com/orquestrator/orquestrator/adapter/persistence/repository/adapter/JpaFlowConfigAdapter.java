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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adaptador JPA para fornecimento de configurações de fluxo.
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
        return repository.findLatestActive(operationType)
                .map(this::mapToDefinition);
    }

    @Override
    @Cacheable(value = "flow_configs_version", key = "#operationType + '-' + #version", unless = "#result == null")
    public Optional<FlowDefinition> getFlow(String operationType, Integer version) {
        return Optional.ofNullable(version)
                .flatMap(v -> repository.findSpecificVersion(operationType, v))
                .or(() -> repository.findLatestActive(operationType))
                .map(this::mapToDefinition);
    }

    private FlowDefinition mapToDefinition(FlowConfigEntity entity) {
        Set<String> requiredOutputs = extractStringSet(entity.getRequiredOutputs());
        Set<TaskReference> allowedTasks = parseJson(entity.getAllowedTasks(), new TypeReference<Set<TaskReference>>() {});

        return new FlowDefinition(
                entity.getOperationType(),
                entity.getVersion(),
                requiredOutputs,
                allowedTasks
        );
    }

    private Set<String> extractStringSet(JsonNode node) {
        if (node == null || !node.isArray()) return Set.of();
        return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toUnmodifiableSet());
    }

    private <T> Set<T> parseJson(JsonNode json, TypeReference<Set<T>> typeReference) {
        if (json == null || !json.isArray()) return Set.of();
        try {
            Set<T> result = objectMapper.convertValue(json, typeReference);
            return result != null ? Set.copyOf(result) : Set.of();
        } catch (Exception e) {
            log.error(STR."Falha ao parsear configuração JSON: \{e.getMessage()}");
            return Set.of();
        }
    }
}
