package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import br.com.orquestrator.orquestrator.tasks.script.groovy.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Provedor de GroovyTask: Padronizado.
 */
@Component
public class GroovyTaskProvider extends TypedTaskProvider<GroovyTaskConfiguration> {

    private final GroovyScriptLoader scriptLoader;
    private final GroovyBindingFactory bindingFactory;

    public GroovyTaskProvider(ObjectMapper objectMapper, GroovyScriptLoader scriptLoader, GroovyBindingFactory bindingFactory) {
        super(objectMapper, GroovyTaskConfiguration.class, "GROOVY_SCRIPT");
        this.scriptLoader = scriptLoader;
        this.bindingFactory = bindingFactory;
    }

    @Override
    protected Task createInternal(TaskDefinition def, GroovyTaskConfiguration config) {
        return new GroovyTask(def, scriptLoader, bindingFactory, config);
    }
}
