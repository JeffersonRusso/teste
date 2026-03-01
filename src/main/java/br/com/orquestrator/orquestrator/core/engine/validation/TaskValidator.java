package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.springframework.stereotype.Component;

/**
 * TaskValidator: Garante que o contrato de dados da tarefa seja respeitado.
 */
@Component
public class TaskValidator {

    public void validate(Pipeline.TaskNode node, ExecutionContext context) {
        for (var input : node.inputs()) {
            if (input.required() && context.get(input.contextKey()) == null) {
                throw new PipelineException("Dado obrigatório '" + input.contextKey() + "' ausente no nó [" + node.nodeId() + "]");
            }
        }
    }
}
