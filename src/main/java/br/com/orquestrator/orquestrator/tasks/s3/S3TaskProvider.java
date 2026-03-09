package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * S3TaskProvider: Fábrica para tarefas S3.
 */
@Component
@RequiredArgsConstructor
public class S3TaskProvider implements TaskProvider {

    private final S3Executor s3Executor;
    private final TaskBindingResolver bindingResolver;

    @Override public String getType() { return "S3"; }

    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(S3TaskConfiguration.class); 
    }

    @Override
    public Task create(TaskDefinition definition) {
        S3TaskConfiguration config = bindingResolver.resolve(definition.config(), Map.of(), S3TaskConfiguration.class);
        return new S3Task(s3Executor, config);
    }
}
