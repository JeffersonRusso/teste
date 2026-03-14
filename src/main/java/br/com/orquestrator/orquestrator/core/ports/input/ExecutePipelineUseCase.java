package br.com.orquestrator.orquestrator.core.ports.input;

import br.com.orquestrator.orquestrator.application.usecase.PipelineExecutionResult;
import br.com.orquestrator.orquestrator.core.ports.input.command.ExecutionCommand;

/**
 * ExecutePipelineUseCase: Porta de Entrada (Input Port).
 */
public interface ExecutePipelineUseCase {
    
    /**
     * Executa um pipeline e retorna um resultado rico em metadados.
     */
    PipelineExecutionResult execute(ExecutionCommand command);
}
