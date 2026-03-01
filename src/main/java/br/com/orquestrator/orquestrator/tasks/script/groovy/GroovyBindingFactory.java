package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import groovy.lang.Binding;
import org.springframework.stereotype.Component;

/**
 * Fábrica de Binding Groovy: Otimizada para Zero-Cópia e Performance.
 */
@Component
public class GroovyBindingFactory {

    /**
     * Cria um binding que expõe o contexto de execução de forma nativa para o script.
     * 
     * @param context O contexto de execução atual.
     * @param definition A definição da task (para metadados se necessário).
     * @return Binding configurado com o LazyBindingMap.
     */
    public Binding createBinding(ExecutionContext context, TaskDefinition definition) {
        Binding binding = new Binding();
        
        // 'ctx' é a porta de entrada universal para o script externo.
        // O LazyBindingMap evita a cópia de dados e permite navegação direta.
        binding.setVariable("ctx", new LazyBindingMap(context));
        
        // Atalhos úteis para o desenvolvedor de script
        binding.setVariable("correlationId", context.getCorrelationId());
        binding.setVariable("nodeId", definition.getNodeId().value());

        return binding;
    }
}
