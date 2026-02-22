package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FlowConfigEntity;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.repository.FlowConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaFlowConfigAdapter implements FlowConfigProvider {

    private final FlowConfigRepository repository;

    @Override
    @Cacheable(value = "flow_definitions", key = "#operationType", unless = "#result == null")
    public Optional<FlowDefinition> getFlow(String operationType) {
        return repository.findLatestActive(operationType)
                .map(this::mapToDomain);
    }

    private FlowDefinition mapToDomain(FlowConfigEntity entity) {
        return new FlowDefinition(
                entity.getOperationType(),
                entity.getVersion(),
                entity.getRequiredOutputs() != null ? Set.copyOf(entity.getRequiredOutputs()) : Set.of(),
                entity.getTasks().stream()
                        .map(flowTask -> new FlowDefinition.TaskReference(
                                flowTask.getId().getTaskId(), 
                                flowTask.getId().getTaskVersion()))
                        .collect(Collectors.toList())
        );
    }
}
