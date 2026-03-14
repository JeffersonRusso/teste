package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

@Component
public class LogResponseInterceptorProvider extends AbstractInterceptorProvider<LogResponseConfig> {

    public LogResponseInterceptorProvider(TaskBindingResolver bindingResolver) {
        super(bindingResolver, LogResponseConfig.class);
    }

    @Override public String getType() { return "logging"; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<LogResponseConfig> config, TaskDefinition taskDef) {
        return new LogResponseInterceptor(config);
    }
}
