/*
package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.core.engine.runtime.SignalSchema;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// CLASSE DESCONTINUADA: A validação de topologia agora é feita pelos CompilationSteps.
@Slf4j
@Component
public class TaskValidator {

    public void validate(TaskDefinition task, SignalSchema schema) {
        List<String> missing = new ArrayList<>();

        task.getInputsAsMap().values().forEach(binding -> {
            String requiredSignal = binding.signalName();
            if (!schema.canProvide(requiredSignal)) {
                missing.add(requiredSignal);
            }
        });

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Tarefa [" + task.nodeId().value() + 
                "] possui dependências não satisfeitas: " + missing);
        }
    }
}
*/
