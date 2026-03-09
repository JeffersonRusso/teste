package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
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
    public TaskResult execute(Map<String, DataValue> inputs) {
        try {
            Script script = scriptClass.getDeclaredConstructor().newInstance();
            
            // Prepara o binding com os dados puros
            Map<String, Object> rawInputs = new HashMap<>();
            inputs.forEach((k, v) -> rawInputs.put(k, v.raw()));
            
            Binding binding = new Binding();
            binding.setVariable("inputs", rawInputs);
            binding.setVariable("ctx", rawInputs);
            binding.setVariable("nodeId", nodeId);
            
            script.setBinding(binding);
            Object result = script.run();
            
            return TaskResult.success(DataValueFactory.of(result));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao executar script Groovy no nó: " + nodeId, e);
        }
    }
}
