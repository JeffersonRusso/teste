package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import groovy.lang.Binding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fábrica de Binding para Groovy: Expõe apenas o necessário.
 */
@Component
@RequiredArgsConstructor
public class GroovyBindingFactory {

    public Binding createBinding(ExecutionContext context, TaskDefinition definition) {
        Binding binding = new Binding();

        // 1. Expõe apenas as variáveis declaradas no 'requires'
        List<DataSpec> requires = definition.getRequires();
        if (requires != null) {
            for (DataSpec spec : requires) {
                binding.setVariable(spec.name(), context.get(spec.name()));
            }
        }

        // 2. Metadados úteis (sem expor o contexto inteiro)
        binding.setVariable("nodeId", definition.getNodeId().value());

        return binding;
    }
}
