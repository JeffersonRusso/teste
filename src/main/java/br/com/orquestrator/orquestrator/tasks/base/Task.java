package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Task: Unidade atômica de trabalho.
 * Stateless e Thread-Safe. Recebe todo o contexto necessário na execução.
 */
@FunctionalInterface
public interface Task {
    TaskResult execute(TaskContext context);
}
