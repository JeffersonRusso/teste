package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * SpelTask: Executa expressões SpEL.
 */
@RequiredArgsConstructor
public class SpelTask implements Task {

    private final ExpressionEngine expressionEngine;
    private final SpelTaskConfiguration config;

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        if (config.expression() == null || config.expression().isBlank()) {
            return TaskResult.success(DataValue.EMPTY);
        }

        DataValue result = expressionEngine.compile(config.expression()).evaluate(inputs);
        return TaskResult.success(result);
    }
}
