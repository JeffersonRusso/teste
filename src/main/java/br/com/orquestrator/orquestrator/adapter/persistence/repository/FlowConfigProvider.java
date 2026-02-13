package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import java.util.Optional;

public interface FlowConfigProvider {
    Optional<FlowDefinition> getFlow(String operationType);
    Optional<FlowDefinition> getFlow(String operationType, Integer version);
}
