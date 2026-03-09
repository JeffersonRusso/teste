package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import java.util.Optional;
import java.util.Set;

public interface PipelineRepository {
    Optional<PipelineDefinition> findActive(String operationType);
    Set<String> findAllActiveOperationTypes();
}
