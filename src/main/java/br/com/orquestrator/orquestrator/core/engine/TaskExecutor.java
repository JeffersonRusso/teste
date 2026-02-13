package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;

/**
 * Contrato para execução de uma tarefa dentro de um fluxo.
 */
public interface TaskExecutor {
    
    void execute(TaskDefinition taskDef, ExecutionContext context, DataBus dataBus);
}
