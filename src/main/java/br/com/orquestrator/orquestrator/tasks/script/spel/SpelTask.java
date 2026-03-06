package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpelTask implements Task, Configurable<SpelTaskConfiguration> {

    private final ExpressionEngine expressionEngine;

    @Override
    public Class<SpelTaskConfiguration> getConfigClass() {
        return SpelTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        SpelTaskConfiguration config = context.getConfig();
        
        if (config.expression() == null || config.expression().isBlank()) {
            return TaskResult.success(new DataValue.Empty());
        }

        DataValue result = expressionEngine.evaluate(config.expression(), context.inputs());
        return TaskResult.success(result);
    }
}
