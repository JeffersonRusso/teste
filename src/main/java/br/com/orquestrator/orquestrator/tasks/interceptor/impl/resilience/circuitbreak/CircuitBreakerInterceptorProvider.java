package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import org.springframework.stereotype.Component;

@Component("CIRCUITBREAK")
public class CircuitBreakerInterceptorProvider extends AbstractInterceptorProvider<CircuitBreakerConfig> {

    public CircuitBreakerInterceptorProvider(TaskBindingResolver bindingResolver) {
        super(bindingResolver, CircuitBreakerConfig.class);
    }

    @Override public int getOrder() { return 70; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<CircuitBreakerConfig> config, TaskDefinition taskDef) {
        return new CircuitBreakerInterceptor(config);
    }
}
