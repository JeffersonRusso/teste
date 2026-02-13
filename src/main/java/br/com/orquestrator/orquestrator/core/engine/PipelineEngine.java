package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;

/**
 * Contrato para o motor de execução de pipelines.
 * Permite trocar a implementação (In-Memory, Distributed, Sequential) sem afetar o domínio.
 */
public interface PipelineEngine {
    void run(ExecutionContext context, Pipeline pipeline);
}
