package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import br.com.orquestrator.orquestrator.tasks.script.groovy.GroovyBindingFactory;
import br.com.orquestrator.orquestrator.tasks.script.groovy.GroovyScriptLoader;
import br.com.orquestrator.orquestrator.tasks.script.groovy.GroovyTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroovyTaskProvider implements TaskProvider {

    private final GroovyScriptLoader scriptLoader;
    private final GroovyBindingFactory bindingFactory;
    private final TaskResultMapper resultMapper;

    @Override
    public String getType() {
        return "GROOVY_SCRIPT";
    }

    @Override
    public Task create(TaskDefinition def) {
        return new GroovyTask(def, scriptLoader, bindingFactory, resultMapper);
    }
}
