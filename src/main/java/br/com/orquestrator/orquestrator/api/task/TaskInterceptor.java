package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;

/**
 * TaskInterceptor: Interface base para todos os middlewares de tarefas.
 * Define um padrão simétrico para todos os comportamentos extras (Log, Retry, Cache, etc.).
 */
public interface TaskInterceptor {
    
    /**
     * Intercepta a execução de uma tarefa.
     * 
     * @param context Contexto de execução (Inputs + Metadata)
     * @param chain A cadeia para prosseguir a execução
     * @return O resultado da tarefa (Success, Failure, etc.)
     */
    TaskResult intercept(TaskExecutionContext context, TaskChain chain);
}
