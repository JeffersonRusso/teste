package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import lombok.RequiredArgsConstructor;

/**
 * BaseTask: Abstração para tasks que dependem de expressões SpEL.
 */
@RequiredArgsConstructor
public abstract class BaseTask<C> implements Task {

    protected final ExpressionService expressionService;
    protected final C config;

    @Override
    public final TaskResult execute(ExecutionContext context) {
        EvaluationContext eval = expressionService.create(context);
        return executeInternal(context, eval);
    }

    protected abstract TaskResult executeInternal(ExecutionContext context, EvaluationContext eval);
}
