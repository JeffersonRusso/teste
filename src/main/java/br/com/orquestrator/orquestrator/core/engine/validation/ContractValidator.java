package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ContractValidator: Valida a integridade do fluxo de dados (Build-time).
 * Garante que todas as dependências de dados sejam satisfeitas.
 */
@Slf4j
@Component
public class ContractValidator {

    public void validate(PipelineDefinition def, List<TaskDefinition> activeTasks) {
        // 1. Coleta todos os dados que serão produzidos (Oferta)
        Set<String> availableData = collectAvailableData(def, activeTasks);

        // 2. Valida se as tasks têm seus inputs satisfeitos (Demanda Interna)
        validateTaskDependencies(activeTasks, availableData);

        // 3. Valida se o pipeline entrega o que prometeu (Demanda Externa)
        validateRequiredOutputs(def, availableData);

        log.debug("Contrato validado com sucesso para a operação: {}", def.operationType());
    }

    private Set<String> collectAvailableData(PipelineDefinition def, List<TaskDefinition> tasks) {
        Set<String> available = new HashSet<>();
        
        // Dados do sistema sempre disponíveis
        available.add(ContextKey.RAW);
        available.add(ContextKey.HEADER);
        available.add(ContextKey.OPERATION_TYPE);

        // Dados da normalização (standard.*)
        if (def.inputMapping() != null) {
            def.inputMapping().keySet().forEach(key -> available.add(ContextKey.STANDARD + "." + key));
        }

        // Dados produzidos pelas tasks
        for (var task : tasks) {
            if (task.outputs() != null) {
                available.addAll(task.outputs().values());
            }
        }
        return available;
    }

    private void validateTaskDependencies(List<TaskDefinition> tasks, Set<String> availableData) {
        for (var task : tasks) {
            if (task.inputs() == null) continue;

            for (String requiredKey : task.inputs().values()) {
                if (!availableData.contains(requiredKey)) {
                    throw new PipelineException(String.format(
                        "Erro de Contrato: A task [%s] requer o dado '%s', mas ele não será produzido.", 
                        task.nodeId().value(), requiredKey));
                }
            }
        }
    }

    private void validateRequiredOutputs(PipelineDefinition def, Set<String> availableData) {
        if (def.defaultRequiredOutputs() == null) return;

        for (String requiredOutput : def.defaultRequiredOutputs()) {
            if (!availableData.contains(requiredOutput)) {
                throw new PipelineException(String.format(
                    "Erro de Contrato: O pipeline para '%s' prometeu entregar '%s', mas esse dado não será produzido.", 
                    def.operationType(), requiredOutput));
            }
        }
    }
}
