package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import groovy.lang.Binding;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * GroovyBindingFactory: Prepara o ambiente de execução para scripts Groovy.
 * Agora desacoplado do ContextHolder e focado no Shadow Context.
 */
@Component
public class GroovyBindingFactory {

    public Binding createBinding(TaskContext context) {
        Binding binding = new Binding();
        
        // Converte o Shadow Context (DataValue) para um mapa simples para o Groovy
        Map<String, Object> rawInputs = new HashMap<>();
        context.inputs().forEach((k, v) -> rawInputs.put(k, v.raw()));
        
        binding.setVariable("inputs", rawInputs);
        binding.setVariable("nodeId", context.nodeId());
        
        // Atalho para facilitar o acesso aos dados no script
        binding.setVariable("ctx", rawInputs);

        return binding;
    }
}
