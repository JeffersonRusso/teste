package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class S3Task implements Task {

    private final S3Executor s3Executor;
    private final CompiledConfiguration<S3TaskConfiguration> config;
    private final ObjectMapper objectMapper;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        S3TaskConfiguration resolved = config.resolve(context.getInputs());

        try {
            // Pega o input e converte para JsonNode (o S3Executor ainda fala Jackson)
            DataNode inputNode = context.getInput("input");
            JsonNode payload = objectMapper.valueToTree(inputNode.asNative());

            return s3Executor.execute(resolved, payload);

        } catch (Exception e) {
            log.error("Erro S3 [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Erro S3: " + e.getMessage());
        }
    }
}
