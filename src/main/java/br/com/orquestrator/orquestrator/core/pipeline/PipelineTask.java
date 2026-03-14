package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.application.usecase.PipelineExecutionResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.input.command.ExecutionCommand;
import br.com.orquestrator.orquestrator.core.ports.input.PipelineRunner;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PipelineTask: Executa sub-pipelines sem acoplamento circular.
 */
@Slf4j
@RequiredArgsConstructor
public final class PipelineTask implements Task {

    private final CompiledConfiguration<PipelineTaskConfiguration> config;
    private final PipelineRunner runner; // Interface funcional (Injeção de Comportamento)
    private final DataFactory dataFactory;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        PipelineTaskConfiguration resolved = config.resolve(context.getInputs());

        try {
            ExecutionCommand command = new ExecutionCommand(
                resolved.operationType(),
                context.getTaskName() + "_SUB",
                null,
                context.getNativeInputs()
            );

            // Chamada direta e limpa
            PipelineExecutionResult result = runner.run(command);

            if (result.success()) {
                return TaskResult.success(dataFactory.createObject(result.output()));
            } else {
                return TaskResult.error(500, "Falha no sub-pipeline: " + result.message());
            }

        } catch (Exception e) {
            log.error("Erro no sub-pipeline [{}]: {}", resolved.operationType(), e.getMessage());
            return TaskResult.error(500, "Falha técnica no sub-pipeline: " + e.getMessage());
        }
    }
}
