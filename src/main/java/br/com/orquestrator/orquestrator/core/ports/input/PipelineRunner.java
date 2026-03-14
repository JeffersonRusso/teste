package br.com.orquestrator.orquestrator.core.ports.input;

import br.com.orquestrator.orquestrator.application.usecase.PipelineExecutionResult;
import br.com.orquestrator.orquestrator.core.ports.input.command.ExecutionCommand;

/**
 * PipelineRunner: Interface funcional para quebra de dependência circular.
 */
@FunctionalInterface
public interface PipelineRunner {
    PipelineExecutionResult run(ExecutionCommand command);
}
