package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSession;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionSessionFactory;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final ExecutionSessionFactory sessionFactory;
    private final SpelContextFactory contextFactory;

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        // 1. Cria a sessão de execução (Encapsula as fases)
        ExecutionSession session = sessionFactory.create(context, pipeline);

        // 2. Prepara a infra de avaliação
        var evalContext = contextFactory.create(context.reader());

        // 3. ESTABELECE O ESCOPO E DISPARA A SESSÃO
        ScopedValue.where(ContextHolder.CONTEXT, context)
                   .where(ContextHolder.EVAL_CONTEXT, evalContext)
                   .run(session::run);
    }
}
