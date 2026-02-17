package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Representa um elo na cadeia de execução de uma task.
 */
@FunctionalInterface
public interface TaskChain {
    TaskResult proceed(ExecutionContext context);
}
