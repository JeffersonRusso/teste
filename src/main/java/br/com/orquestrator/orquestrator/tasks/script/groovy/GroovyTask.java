package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * GroovyTask: Executa scripts Groovy pré-compilados.
 */
@RequiredArgsConstructor
public class GroovyTask implements Task {

    private final Class<? extends Script> scriptClass;
    private final String nodeId;

    @Override
    public TaskResult execute(Map<String, JsonNode> inputs) {
        try {
            Script script = scriptClass.getDeclaredConstructor().newInstance();
            
            Map<String, Object> rawInputs = new HashMap<>();
            inputs.forEach((k, v) -> rawInputs.put(k, v));
            
            Binding binding = new Binding();
            binding.setVariable("inputs", rawInputs);
            binding.setVariable("ctx", rawInputs);
            binding.setVariable("nodeId", nodeId);
            
            script.setBinding(binding);
            Object result = script.run();
            
            return TaskResult.success(JsonNodeFactory.instance.pojoNode(result));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao executar script Groovy no nó: " + nodeId, e);
        }
    }
}
