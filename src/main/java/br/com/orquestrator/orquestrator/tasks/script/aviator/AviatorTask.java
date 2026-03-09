package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * AviatorTask: Executa scripts Aviator pré-compilados.
 */
@RequiredArgsConstructor
public class AviatorTask implements Task {

    private final Expression compiledScript;

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        Map<String, Object> rawInputs = new HashMap<>();
        inputs.forEach((k, v) -> rawInputs.put(k, v.raw()));

        Object result = compiledScript.execute(rawInputs);
        
        return TaskResult.success(DataValueFactory.of(result));
    }
}
