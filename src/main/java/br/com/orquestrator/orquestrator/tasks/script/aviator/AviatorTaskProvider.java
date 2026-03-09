package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.script.ScriptTaskConfiguration;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * AviatorTaskProvider: Fábrica para tarefas Aviator.
 * Compila o script no startup para performance máxima.
 */
@Component
@RequiredArgsConstructor
public class AviatorTaskProvider implements TaskProvider {

    private final TaskBindingResolver bindingResolver;

    @Override public String getType() { return "AVIATOR"; }
    
    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(ScriptTaskConfiguration.class); 
    }

    @Override
    public Task create(TaskDefinition definition) {
        ScriptTaskConfiguration config = bindingResolver.resolve(definition.config(), Map.of(), ScriptTaskConfiguration.class);
        
        // Compila o script uma única vez no startup
        Expression compiledScript = AviatorEvaluator.compile(config.script(), true);
        
        return new AviatorTask(compiledScript);
    }
}
