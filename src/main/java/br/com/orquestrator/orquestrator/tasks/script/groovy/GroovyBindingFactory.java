package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroovyBindingFactory {

    private final ObjectMapper objectMapper;

    public Binding createBinding(ExecutionContext context, TaskDefinition definition) {
        Binding binding = new Binding();

        List<DataSpec> requires = definition.getRequires();
        if (requires != null) {
            for (DataSpec spec : requires) {
                String inputVar = spec.name();
                binding.setVariable(inputVar, context.get(inputVar));
            }
        }

        binding.setVariable(ContextKey.JSON_MAPPER, objectMapper);
        binding.setVariable("context", context);
        binding.setVariable("__NODE_ID__", definition.getNodeId().value());

        return binding;
    }
}
