package br.com.orquestrator.orquestrator.tasks.base;

/**
 * TaskChain: Elo na cadeia de execução.
 */
@FunctionalInterface
public interface TaskChain {
    TaskResult proceed();
}
