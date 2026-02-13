package br.com.orquestrator.orquestrator.core.engine.listener;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

public interface TaskExecutionListener {
    void onStart(TaskDefinition taskDef, ExecutionContext context);
    void onSuccess(TaskDefinition taskDef, ExecutionContext context);
    void onError(TaskDefinition taskDef, ExecutionContext context, Exception e);
}
