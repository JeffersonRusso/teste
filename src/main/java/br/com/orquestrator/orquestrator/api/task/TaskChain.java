package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;

/**
 * TaskChain: Contrato que permite que um interceptor passe a execução para o próximo elo.
 */
public interface TaskChain {
    
    /**
     * Avança na cadeia de execução.
     */
    TaskResult proceed(TaskExecutionContext context);
}
