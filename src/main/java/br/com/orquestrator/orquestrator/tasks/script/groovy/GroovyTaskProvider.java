package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import org.springframework.stereotype.Component;

@Component
public class GroovyTaskProvider implements TaskProvider {
    @Override public String getType() { return "GROOVY_SCRIPT"; }
    @Override public Task create(TaskDefinition definition) { return new GroovyTask(); }
}
