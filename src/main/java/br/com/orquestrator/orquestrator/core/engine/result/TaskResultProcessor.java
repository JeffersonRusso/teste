package br.com.orquestrator.orquestrator.core.engine.result;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * Contrato para processadores de resultado de task.
 */
public interface TaskResultProcessor {
    void process(TaskResult result, TaskDefinition definition, ExecutionContext context);
}
