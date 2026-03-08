package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Task: Unidade atômica de trabalho.
 * Stateless e Thread-Safe. Recebe o Shadow Context (Inputs Tipados).
 */
@FunctionalInterface
public interface Task {
    /**
     * Executa a tarefa com os inputs tipados e retorna o resultado.
     * O TaskContext agora carrega Map<String, DataValue>.
     */
    TaskResult execute(TaskContext context);
}
