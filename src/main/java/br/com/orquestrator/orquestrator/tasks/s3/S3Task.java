package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

/**
 * S3Task: Função pura de exportação.
 */
@RequiredArgsConstructor
public class S3Task implements Task {

    private final S3Executor executor;
    private final ExpressionService expressionService;
    private final S3TaskConfiguration config;

    @Override
    public TaskResult execute(ExecutionContext context) {
        EvaluationContext evalContext = expressionService.create(context);
        
        Object content = evalContext.evaluate(config.contentExpression(), Object.class);
        if (content == null) return TaskResult.success(null);

        String key = evalContext.resolve(config.keyTemplate(), String.class);

        return executor.upload(config.bucket(), key, config.region(), content);
    }
}
