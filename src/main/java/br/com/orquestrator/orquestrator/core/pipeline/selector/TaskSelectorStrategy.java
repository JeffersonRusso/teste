package br.com.orquestrator.orquestrator.core.pipeline.selector;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;

public interface TaskSelectorStrategy {
    boolean shouldRun(TaskDefinition task, ExecutionContext context);
}
