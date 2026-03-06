package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class S3Task implements Task, Configurable<S3TaskConfiguration> {

    private final S3Executor s3Executor;

    @Override
    public Class<S3TaskConfiguration> getConfigClass() {
        return S3TaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        S3TaskConfiguration config = context.getConfig();
        
        return s3Executor.upload(
            config.bucket(),
            config.key(),
            config.region(),
            config.content()
        );
    }
}
