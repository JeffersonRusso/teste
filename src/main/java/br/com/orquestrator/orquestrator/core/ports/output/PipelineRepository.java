package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import java.util.Optional;
import java.util.Set;

/**
 * PipelineRepository: A porta de saída para carregar definições de pipeline.
 */
public interface PipelineRepository {
    Optional<PipelineDefinition> findActive(String operationType);
    Set<String> findAllActiveOperationTypes();
}
