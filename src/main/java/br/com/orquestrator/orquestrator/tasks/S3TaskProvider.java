package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import br.com.orquestrator.orquestrator.tasks.s3.S3Executor;
import br.com.orquestrator.orquestrator.tasks.s3.S3Task;
import br.com.orquestrator.orquestrator.tasks.s3.S3TaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class S3TaskProvider extends TypedTaskProvider<S3TaskConfiguration> {

    private final S3Executor executor;
    private final ExpressionService expressionService;

    public S3TaskProvider(ObjectMapper objectMapper, S3Executor executor, ExpressionService expressionService) {
        super(objectMapper, S3TaskConfiguration.class, "S3");
        this.executor = executor;
        this.expressionService = expressionService;
    }

    @Override
    protected Task createInternal(TaskDefinition def, S3TaskConfiguration config) {
        return new S3Task(executor, expressionService, config);
    }
}
