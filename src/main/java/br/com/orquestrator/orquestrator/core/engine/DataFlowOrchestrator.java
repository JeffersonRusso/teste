package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Deprecated
public class DataFlowOrchestrator implements PipelineEngine {

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        // Este motor foi substituído pela ExecutionSession.
        // Mantido apenas para compatibilidade de interface durante a transição.
        throw new UnsupportedOperationException("Use ExecutionSession para executar pipelines.");
    }
}
