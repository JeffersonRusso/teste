package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * TaskDecorator: Define um comportamento que envolve a execução de uma tarefa.
 */
@FunctionalInterface
public interface TaskDecorator {
    /**
     * Aplica a lógica de decoração.
     * @param next O próximo elo na cadeia (pode ser outro decorador ou a task final).
     * @return O resultado da execução.
     */
    TaskResult apply(TaskChain next);
}
