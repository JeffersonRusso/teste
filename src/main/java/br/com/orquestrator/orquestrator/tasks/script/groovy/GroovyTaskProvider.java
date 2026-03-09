package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * GroovyTaskProvider: Fábrica para tarefas Groovy.
 * Compila o script no startup para evitar overhead de parsing em runtime.
 */
@Component
@RequiredArgsConstructor
public class GroovyTaskProvider implements TaskProvider {

    private final TaskBindingResolver bindingResolver;
    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    @Override public String getType() { return "GROOVY_SCRIPT"; }

    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(ScriptTaskConfiguration.class); 
    }

    @Override
    @SuppressWarnings("unchecked")
    public Task create(TaskDefinition definition) {
        ScriptTaskConfiguration config = bindingResolver.resolve(definition.config(), Map.of(), ScriptTaskConfiguration.class);
        
        // Compila o script para uma classe Java real no startup
        Class<? extends Script> scriptClass = groovyClassLoader.parseClass(config.script());
        
        return new GroovyTask(scriptClass, definition.nodeId().value());
    }
}
