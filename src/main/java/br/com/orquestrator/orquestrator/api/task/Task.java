package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;

/**
 * Task: O contrato atômico de processamento no Domínio.
 * 
 * Aplicando o princípio de Simetria e Unificação de Execução:
 * - Agora recebe um TaskExecutionContext completo em vez de um simples Map.
 * - Isso dá à Task acesso aos seus metadados, definições e estado de execução.
 */
@FunctionalInterface
public interface Task {
    
    /**
     * Executa a lógica de negócio principal da tarefa.
     * 
     * @param context Contexto de execução rico em informações
     * @return O resultado da tarefa (Success, Failure, Skipped)
     */
    TaskResult execute(TaskExecutionContext context);
}
