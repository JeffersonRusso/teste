package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ContractValidator: Garante a integridade do fluxo de dados do pipeline.
 * Verifica se todos os inputs requeridos pelas tasks serão produzidos por alguém.
 */
@Slf4j
@Component
public class ContractValidator {

    public void validate(PipelineDefinition def, List<TaskDefinition> activeTasks) {
        // 1. Mapeia todos os dados que estarão disponíveis (Ofertas)
        Set<String> availableData = new HashSet<>();
        
        // Dados vindos da normalização inicial
        if (def.inputMapping() != null) {
            def.inputMapping().keySet().forEach(key -> availableData.add("standard." + key));
        }
        
        // Dados produzidos pelas tasks que realmente vão rodar
        for (var task : activeTasks) {
            if (task.outputs() != null) {
                availableData.addAll(task.outputs().values());
            }
        }

        // 2. Verifica se as demandas das tasks são atendidas
        for (var task : activeTasks) {
            if (task.inputs() != null) {
                for (String requiredKey : task.inputs().values()) {
                    // Se o dado não é produzido por ninguém e não é o 'raw' inicial
                    if (!availableData.contains(requiredKey) && !"raw".equals(requiredKey)) {
                        throw new PipelineException(
                            String.format("Erro de Contrato: A task [%s] requer o dado '%s', mas ele não é produzido por nenhuma tarefa anterior nem pelo mapeamento de entrada.", 
                            task.nodeId().value(), requiredKey)
                        );
                    }
                }
            }
        }
        
        log.debug("Validação de contrato concluída com sucesso para o pipeline: {}", def.operationType());
    }
}
