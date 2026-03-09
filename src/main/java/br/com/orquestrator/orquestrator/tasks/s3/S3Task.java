package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * S3Task: Executa uploads para o S3.
 */
@RequiredArgsConstructor
public class S3Task implements Task {

    private final S3Executor s3Executor;
    private final S3TaskConfiguration config;

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        return s3Executor.upload(
            config.bucket(),
            config.key(),
            config.region(),
            config.content()
        );
    }
}
