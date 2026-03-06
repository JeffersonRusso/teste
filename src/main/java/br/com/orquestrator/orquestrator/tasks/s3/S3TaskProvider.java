package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3TaskProvider implements TaskProvider {

    private final S3Executor s3Executor;

    @Override
    public String getType() {
        return "S3";
    }

    @Override
    public Task create(TaskDefinition definition) {
        return new S3Task(s3Executor);
    }
}
