package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

@Component("RETRY")
public class RetryInterceptorProvider extends AbstractInterceptorProvider<RetryConfig> {

    public RetryInterceptorProvider(TaskBindingResolver bindingResolver) {
        super(bindingResolver, RetryConfig.class);
    }

    @Override public int getOrder() { return 60; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<RetryConfig> config, TaskDefinition taskDef) {
        return new RetryInterceptor(config);
    }
}
