package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * SpelTaskProvider: Fábrica para tarefas SpEL.
 */
@Component
@RequiredArgsConstructor
public class SpelTaskProvider implements TaskProvider {

    private final ExpressionEngine expressionEngine;
    private final TaskBindingResolver bindingResolver;

    @Override public String getType() { return "SPEL"; }
    
    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(SpelTaskConfiguration.class); 
    }

    @Override
    public Task create(TaskDefinition definition) {
        SpelTaskConfiguration config = bindingResolver.resolve(definition.config(), Map.of(), SpelTaskConfiguration.class);
        return new SpelTask(expressionEngine, config);
    }
}
