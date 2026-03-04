package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskValidator {

    public void validate(TaskDefinition def, Map<String, Object> resolvedInputs) {
        if (def.inputs() == null) return;

        def.inputs().forEach((localKey, globalKey) -> {
            // Verifica se o dado obrigatório está presente no mapa resolvido
            if (resolvedInputs.get(localKey) == null) {
                throw new PipelineException(
                    String.format("Erro de Contrato: A task [%s] requer o dado '%s' (mapeado de '%s'), mas ele está nulo.", 
                    def.nodeId().value(), localKey, globalKey)
                );
            }
        });
    }
}
