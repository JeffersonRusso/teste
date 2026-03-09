package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;

/**
 * PipelineLoader: Interface para carregar definições de pipeline da persistência.
 */
public interface PipelineLoader {
    PipelineDefinition loadDefinition(String operationType);
}
