package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroovyTask implements Task {

    private final Class<? extends Script> scriptClass;
    private final Binding binding; // Recebe o binding já resolvido

    @Override
    public TaskResult execute() {
        try {
            Script script = scriptClass.getConstructor(Binding.class).newInstance(binding);
            Object result = script.run();
            return TaskResult.success(result);
        } catch (Exception e) {
            throw new RuntimeException("Erro na execução do script Groovy: " + e.getMessage(), e);
        }
    }
}
