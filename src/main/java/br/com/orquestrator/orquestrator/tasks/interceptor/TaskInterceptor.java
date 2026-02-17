package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * Contrato para interceptores de tasks.
 */
public interface TaskInterceptor {
    TaskResult intercept(ExecutionContext context, TaskChain next, Object config, TaskDefinition taskDef);
    
    default Class<?> getConfigClass() {
        return null;
    }
}
