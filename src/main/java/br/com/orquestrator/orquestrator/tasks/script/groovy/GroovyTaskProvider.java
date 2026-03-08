package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GroovyTaskProvider: Fábrica para tarefas Groovy.
 */
@Component
@RequiredArgsConstructor
public class GroovyTaskProvider implements TaskProvider {

    private final GroovyBindingFactory bindingFactory;

    @Override public String getType() { return "GROOVY_SCRIPT"; }

    @Override public Task create(TaskDefinition definition) { 
        return new GroovyTask(bindingFactory); 
    }
}
