package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

/**
 * SpelTask: Executa uma expressão SpEL como uma tarefa core.
 */
@RequiredArgsConstructor
public class SpelTask implements Task {

    private final ExpressionEngine expressionEngine;
    private final TaskDefinition definition;

    @Override
    public TaskResult execute() {
        // Pega a expressão do mapa de configuração
        String expression = (String) definition.config().get("expression");
        
        if (expression == null || expression.isBlank()) {
            return TaskResult.success(null);
        }

        // Avalia a expressão usando o motor unificado e o contexto do escopo
        Object result = expressionEngine.evaluate(expression, ContextHolder.reader(), Object.class);
        
        return TaskResult.success(result);
    }
}
