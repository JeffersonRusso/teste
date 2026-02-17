package br.com.orquestrator.orquestrator.tasks.script;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

/**
 * SpelTask: Função pura de avaliação.
 */
@RequiredArgsConstructor
public class SpelTask implements Task {

    private final TaskDefinition definition;
    private final ExpressionService expressionService;
    private final SpelTaskConfiguration config;

    @Override
    public TaskResult execute(ExecutionContext context) {
        try {
            EvaluationContext evalContext = expressionService.create(context);
            Object result = evalContext.evaluate(config.expression(), Object.class);

            if (result == null && config.required()) {
                throw new PipelineException("Resultado da expressão SpEL é nulo, mas era obrigatório: " + config.expression());
            }
            return TaskResult.success(result);
        } catch (Exception e) {
            throw new PipelineException("Erro ao avaliar expressão SpEL: " + config.expression(), e)
                    .withNodeId(definition.getNodeId().value());
        }
    }
}
