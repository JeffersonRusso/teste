package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.core.engine.runtime.SignalSchema;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ContractValidator: Valida a integridade do grafo de sinais (Dataflow).
 * SRP: Atua como o juiz que coordena a validação, delegando a inteligência para o SignalSchema.
 */
@Slf4j
@Component
public class ContractValidator {

    public void validate(PipelineDefinition def, List<TaskDefinition> activeTasks) {
        SignalSchema schema = new SignalSchema();
        
        // 1. O input inicial 'raw' sempre está disponível
        schema.register("raw");

        // 2. Registra todos os sinais que serão produzidos pelas tasks
        activeTasks.forEach(task -> {
            if (task.outputs() != null) {
                task.outputs().values().forEach(schema::register);
            }
        });

        // 3. Valida se as dependências de cada task estão satisfeitas
        validateTaskDependencies(activeTasks, schema);

        // 4. Valida se os outputs prometidos pelo pipeline serão entregues
        validateRequiredOutputs(def, schema);

        log.debug("Contrato validado com sucesso para a operação: {}", def.operationType());
    }

    private void validateTaskDependencies(List<TaskDefinition> tasks, SignalSchema schema) {
        for (var task : tasks) {
            if (task.inputs() == null) continue;

            for (String required : task.inputs().values()) {
                if (!schema.canProvide(required)) {
                    throw new PipelineException(String.format(
                        "Erro de Contrato: A task [%s] requer o dado '%s', mas ele não será produzido.", 
                        task.nodeId().value(), required));
                }
            }
        }
    }

    private void validateRequiredOutputs(PipelineDefinition def, SignalSchema schema) {
        if (def.defaultRequiredOutputs() == null) return;

        for (String required : def.defaultRequiredOutputs()) {
            if (!schema.canProvide(required)) {
                throw new PipelineException(String.format(
                    "Erro de Contrato: O pipeline para '%s' prometeu entregar '%s', mas esse dado não será produzido.", 
                    def.operationType(), required));
            }
        }
    }
}
