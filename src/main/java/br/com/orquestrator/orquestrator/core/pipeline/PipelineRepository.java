package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import java.util.Optional;

public interface PipelineRepository {
    /**
     * Busca a definição ativa do pipeline para o tipo de operação.
     */
    Optional<PipelineDefinition> findActive(String operationType);
}
