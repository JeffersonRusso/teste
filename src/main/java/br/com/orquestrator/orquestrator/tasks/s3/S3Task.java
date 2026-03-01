package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class S3Task implements Task {

    private final S3Executor executor;
    private final S3TaskConfiguration config;

    @Override
    public TaskResult execute() {
        if (config.content() == null) {
            return TaskResult.success(null);
        }
        return executor.upload(config.bucket(), config.key(), config.region(), config.content());
    }
}
