package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroovyTask implements Task, Configurable<ScriptTaskConfiguration> {

    private final GroovyShell shell = new GroovyShell();

    @Override
    public Class<ScriptTaskConfiguration> getConfigClass() {
        return ScriptTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        ScriptTaskConfiguration config = context.getConfig();
        
        Binding binding = new Binding();
        if (context.inputs() != null) {
            context.inputs().forEach(binding::setVariable);
        }

        Object result = shell.evaluate(config.script());
        return TaskResult.success(DataValue.of(result));
    }
}
