package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class S3TaskProvider extends AbstractTaskProvider<S3TaskConfiguration> {

    private final S3Executor s3Executor;
    private final ObjectMapper objectMapper;

    public S3TaskProvider(TaskBindingResolver bindingResolver, S3Executor s3Executor, ObjectMapper objectMapper) {
        super(bindingResolver, S3TaskConfiguration.class);
        this.s3Executor = s3Executor;
        this.objectMapper = objectMapper;
    }

    @Override public String getType() { return "S3"; }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<S3TaskConfiguration> config) {
        return new S3Task(s3Executor, config, objectMapper);
    }
}
