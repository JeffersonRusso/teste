package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Task: Contrato fundamental de execução.
 * O contexto é acessado via ScopedValue (ContextHolder.CONTEXT).
 */
@FunctionalInterface
public interface Task {
    TaskResult execute();
}
