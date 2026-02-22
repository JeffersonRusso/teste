package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.domain.model.FlowConfig;
import java.util.Optional;

public interface FlowConfigProvider {
    Optional<FlowConfig> getFlow(String operationType);
    Optional<FlowConfig> getFlow(String operationType, Integer version);
}
