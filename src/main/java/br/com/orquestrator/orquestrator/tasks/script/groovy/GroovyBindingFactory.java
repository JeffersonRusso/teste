package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import groovy.lang.Binding;
import org.springframework.stereotype.Component;

@Component
public class GroovyBindingFactory {

    public <T extends ReadableContext & WriteableContext> Binding createBinding(T context, TaskDefinition definition) {
        Binding binding = new Binding();
        
        binding.setVariable("ctx", new LazyBindingMap(context, context));
        
        // Usa o ContextHolder para metadados globais
        binding.setVariable("correlationId", ContextHolder.metadata().getCorrelationId());
        binding.setVariable("nodeId", definition.nodeId().value());

        return binding;
    }
}
