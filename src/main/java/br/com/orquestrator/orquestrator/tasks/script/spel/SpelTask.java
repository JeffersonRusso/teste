package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

/**
 * SpelTask: Executa avaliações SpEL puras.
 */
@RequiredArgsConstructor
public class SpelTask implements Task {

    private final TaskDefinition definition;
    private final ExpressionService expressionService;
    private final SpelTaskConfiguration config;

    @Override
    public TaskResult execute() {
        try {
            // Usa o serviço de expressão soberano
            Object result = expressionService.evaluate(config.expression(), Object.class);

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
