package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AviatorTask implements Task, Configurable<ScriptTaskConfiguration> {

    @Override
    public Class<ScriptTaskConfiguration> getConfigClass() {
        return ScriptTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        ScriptTaskConfiguration config = context.getConfig();
        Object result = AviatorEvaluator.execute(config.script(), context.inputs());
        return TaskResult.success(DataValue.of(result));
    }
}
