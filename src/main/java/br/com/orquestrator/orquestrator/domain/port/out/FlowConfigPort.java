package br.com.orquestrator.orquestrator.domain.port.out;

import br.com.orquestrator.orquestrator.domain.model.FlowConfig;
import java.util.Optional;

public interface FlowConfigPort {
    Optional<FlowConfig> getFlow(String operationType);
    Optional<FlowConfig> getFlow(String operationType, Integer version);
}
