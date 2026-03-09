package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
    public TaskResult execute(Map<String, JsonNode> inputs) {
        Map<String, Object> rawInputs = new HashMap<>();
        inputs.forEach((k, v) -> rawInputs.put(k, v));

        Object result = compiledScript.execute(rawInputs);
        
        return TaskResult.success(JsonNodeFactory.instance.pojoNode(result));
    }
}
