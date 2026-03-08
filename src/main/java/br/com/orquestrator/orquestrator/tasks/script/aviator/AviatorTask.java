package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * AviatorTask: Executa scripts Aviator usando o Shadow Context.
 */
@RequiredArgsConstructor
public class AviatorTask implements Task, Configurable<ScriptTaskConfiguration> {

    @Override
    public Class<ScriptTaskConfiguration> getConfigClass() {
        return ScriptTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        ScriptTaskConfiguration config = context.getConfig();
        
        Map<String, Object> rawInputs = new HashMap<>();
        context.inputs().forEach((k, v) -> rawInputs.put(k, v.raw()));

        Object result = AviatorEvaluator.execute(config.script(), rawInputs);
        
        return TaskResult.success(DataValueFactory.of(result));
    }
}
