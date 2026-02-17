package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;

/**
 * GroovyTask: Função pura de script.
 */
@RequiredArgsConstructor
public class GroovyTask implements Task {

    private final TaskDefinition definition;
    private final GroovyScriptLoader scriptLoader;
    private final GroovyBindingFactory bindingFactory;
    private final GroovyTaskConfiguration config;

    @Override
    public TaskResult execute(ExecutionContext context) {
        Class<? extends Script> scriptClass = (config.scriptBody() != null) 
            ? scriptLoader.loadFromSource("inline:" + definition.getNodeId().value(), config.scriptBody())
            : scriptLoader.loadFromFile(config.scriptName());

        Binding binding = bindingFactory.createBinding(context, definition);
        
        try {
            Script script = scriptClass.getConstructor(Binding.class).newInstance(binding);
            Object result = script.run();
            return TaskResult.success(result);
        } catch (Exception e) {
            throw new RuntimeException(STR."Erro no script \{definition.getNodeId()}: \{e.getMessage()}", e);
        }
    }
}
