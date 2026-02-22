package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import lombok.RequiredArgsConstructor;

/**
 * BaseTask: Abstração para tasks que dependem de expressões SpEL.
 * Otimizado: O EvaluationContext é cacheado no ExecutionContext para ser reutilizado entre tasks.
 */
@RequiredArgsConstructor
public abstract class BaseTask<C> implements Task {

    protected final ExpressionService expressionService;
    protected final C config;

    @Override
    public final TaskResult execute(ExecutionContext context) {
        // Recupera ou cria o contexto de avaliação uma única vez por request
        EvaluationContext eval = context.computeAttachmentIfAbsent(EvaluationContext.class, expressionService::create);
        return executeInternal(context, eval);
    }

    protected abstract TaskResult executeInternal(ExecutionContext context, EvaluationContext eval);
}
