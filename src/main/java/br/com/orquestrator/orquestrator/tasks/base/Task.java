package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Representa uma unidade de trabalho atômica.
 * Opera sobre uma visão restrita de dados (TaskData).
 */
public interface Task {
    /**
     * Executa a lógica da task.
     * @param data Interface para leitura de inputs e escrita de outputs.
     */
    void execute(TaskData data);
}
