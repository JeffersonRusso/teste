package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Representa um elo na cadeia de execução de uma task.
 */
@FunctionalInterface
public interface TaskChain {
    /**
     * Prossegue para o próximo elo da cadeia (interceptor ou task final).
     * @param data O contrato de dados da task.
     */
    void proceed(TaskData data);
}
