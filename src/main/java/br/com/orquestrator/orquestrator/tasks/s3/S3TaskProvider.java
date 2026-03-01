package br.com.orquestrator.orquestrator.tasks.s3;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3TaskProvider implements TaskProvider {

    private final S3Executor executor;
    private final TaskBindingResolver taskBindingResolver;

    @Override
    public String getType() {
        return "S3";
    }

    @Override
    public Task create(TaskDefinition def) {
        return () -> {
            // Resolve a configuração (bucket, key, content) de forma transparente
            var resolvedConfig = taskBindingResolver.resolve(def.config(), S3TaskConfiguration.class);
            return new S3Task(executor, resolvedConfig).execute();
        };
    }
}
