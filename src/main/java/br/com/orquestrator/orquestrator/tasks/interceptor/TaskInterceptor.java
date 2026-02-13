package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;

/**
 * Contrato para interceptores de tasks.
 */
public interface TaskInterceptor {

    /**
     * Intercepta a execução de uma task.
     */
    void intercept(TaskData data, TaskChain next, Object config, TaskDefinition taskDef);
    
    /**
     * Retorna a classe de configuração esperada.
     */
    default Class<?> getConfigClass() {
        return null;
    }
}
