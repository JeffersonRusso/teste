package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;

/**
 * Contrato para interceptores de tasks.
 */
public interface TaskInterceptor {

    /**
     * Intercepta a execução e retorna o resultado da task.
     */
    Object intercept(ExecutionContext context, TaskChain next, Object config, TaskDefinition taskDef);
    
    default Class<?> getConfigClass() {
        return null;
    }
}
