package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.normalize.InputNormalizer;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.core.engine.runtime.ReactiveExecutionEngine;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * DataFlowOrchestrator: Orquestrador de alto nível do motor.
 */
@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final InputNormalizer inputNormalizer;
    private final ReactiveExecutionEngine executionEngine;
    private final PipelineEventPublisher eventPublisher;
    private final SpelContextFactory contextFactory;

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        // 1. Cria o contexto de avaliação SpEL soberano
        var evalContext = contextFactory.create(context);

        // 2. Estabelece o escopo soberano
        ScopedValue.where(ContextHolder.CONTEXT, context)
                   .where(ContextHolder.EVAL_CONTEXT, evalContext)
                   .run(() -> {
            var success = false;
            try {
                inputNormalizer.normalize(context, pipeline);
                executionEngine.execute(pipeline);
                success = true;
            } catch (Exception e) {
                throw (e instanceof PipelineException pe) ? pe : new PipelineException(e.getMessage(), e);
            } finally {
                eventPublisher.publishFinished(context, success);
            }
        });
    }
}
