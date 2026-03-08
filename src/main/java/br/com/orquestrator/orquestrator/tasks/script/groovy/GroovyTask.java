package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;

/**
 * GroovyTask: Executa scripts Groovy usando o Shadow Context.
 */
@RequiredArgsConstructor
public class GroovyTask implements Task, Configurable<ScriptTaskConfiguration> {

    private final GroovyBindingFactory bindingFactory;
    private final GroovyShell shell = new GroovyShell();

    @Override
    public Class<ScriptTaskConfiguration> getConfigClass() {
        return ScriptTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        ScriptTaskConfiguration config = context.getConfig();
        
        // Prepara o ambiente do script com os dados do Shadow Context
        Binding binding = bindingFactory.createBinding(context);
        
        // Executa o script
        Object result = shell.evaluate(config.script());
        
        return TaskResult.success(DataValueFactory.of(result));
    }
}
