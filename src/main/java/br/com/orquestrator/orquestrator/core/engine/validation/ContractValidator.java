package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContractValidator {

    public void validate(PipelineDefinition def, List<TaskDefinition> activeTasks) {
        Set<DataPath> availablePaths = collectAvailablePaths(def, activeTasks);
        validateTaskDependencies(activeTasks, availablePaths);
        validateRequiredOutputs(def, availablePaths);

        log.debug("Contrato validado com sucesso para a operação: {}", def.operationType());
    }

    private Set<DataPath> collectAvailablePaths(PipelineDefinition def, List<TaskDefinition> tasks) {
        Set<DataPath> available = new HashSet<>();
        
        available.add(DataPath.of(ContextKey.RAW));
        available.add(DataPath.of(ContextKey.HEADER));
        available.add(DataPath.of(ContextKey.OPERATION_TYPE));

        if (def.inputMapping() != null) {
            def.inputMapping().keySet().forEach(key -> available.add(DataPath.of(ContextKey.STANDARD + "." + key)));
        }

        for (var task : tasks) {
            if (task.outputs() != null) {
                task.outputs().values().forEach(out -> available.add(DataPath.of(out)));
            }
        }
        return available;
    }

    private void validateTaskDependencies(List<TaskDefinition> tasks, Set<DataPath> availablePaths) {
        for (var task : tasks) {
            if (task.inputs() == null) continue;

            for (String required : task.inputs().values()) {
                DataPath requiredPath = DataPath.of(required);
                if (!isPathSatisfied(requiredPath, availablePaths)) {
                    throw new PipelineException(String.format(
                        "Erro de Contrato: A task [%s] requer o dado '%s', mas ele não será produzido.", 
                        task.nodeId().value(), required));
                }
            }
        }
    }

    private boolean isPathSatisfied(DataPath required, Set<DataPath> available) {
        // Um caminho é satisfeito se ele mesmo existe ou se algum pai dele existe
        return available.stream().anyMatch(prov -> prov.provides(required));
    }

    private void validateRequiredOutputs(PipelineDefinition def, Set<DataPath> availablePaths) {
        if (def.defaultRequiredOutputs() == null) return;

        for (String required : def.defaultRequiredOutputs()) {
            if (!isPathSatisfied(DataPath.of(required), availablePaths)) {
                throw new PipelineException(String.format(
                    "Erro de Contrato: O pipeline para '%s' prometeu entregar '%s', mas esse dado não será produzido.", 
                    def.operationType(), required));
            }
        }
    }
}
