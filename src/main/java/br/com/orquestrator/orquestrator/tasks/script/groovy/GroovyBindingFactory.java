package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroovyBindingFactory {

    private final ObjectMapper objectMapper;

    public Binding createBinding(TaskData data, TaskDefinition definition) {
        Binding binding = new Binding();

        // A. Injeção de Dependências (Valores brutos para facilidade no script)
        List<DataSpec> requires = definition.getRequires();
        if (requires != null) {
            for (int i = 0; i < requires.size(); i++) {
                String inputVar = requires.get(i).name();
                // Passamos o valor desempacotado para que o script use variáveis puras
                binding.setVariable(inputVar, data.get(inputVar).unwrap());
            }
        }

        // B. Injeção de Ferramentas
        binding.setVariable(ContextKey.JSON_MAPPER, objectMapper);
        
        // C. Injeção do TaskData (A instância real, para que o cast no OrchestratorScript funcione)
        binding.setVariable("data", data);
        
        // D. Injeção do Node ID
        binding.setVariable("__NODE_ID__", definition.getNodeId().value());

        return binding;
    }
}
