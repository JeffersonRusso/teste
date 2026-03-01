package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroovyTaskProvider implements TaskProvider {

    private final GroovyScriptLoader scriptLoader;
    private final GroovyBindingFactory bindingFactory;
    private final TaskBindingResolver taskBindingResolver;

    @Override
    public String getType() {
        return "GROOVY_SCRIPT";
    }

    @Override
    public Task create(TaskDefinition def) {
        return () -> {
            // 1. Resolve a configuração (scriptName, scriptBody)
            var config = taskBindingResolver.resolve(def.config(), GroovyTaskConfiguration.class);
            
            // 2. Carrega a classe do script
            Class<? extends Script> scriptClass = (config.scriptBody() != null) 
                ? scriptLoader.loadFromSource("inline:" + def.nodeId().value(), config.scriptBody())
                : scriptLoader.loadFromFile(config.scriptName());

            // 3. Prepara o binding usando o contexto soberano
            Binding binding = bindingFactory.createBinding(ContextHolder.CONTEXT.get(), def);

            // 4. Executa a task pura
            return new GroovyTask(scriptClass, binding).execute();
        };
    }
}
