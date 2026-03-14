package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("OBSERVABILITY")
public class ObservabilityInterceptorProvider extends AbstractInterceptorProvider<Map<String, Object>> {

    private final ApplicationEventPublisher eventPublisher;

    public ObservabilityInterceptorProvider(TaskBindingResolver bindingResolver, ApplicationEventPublisher eventPublisher) {
        super(bindingResolver, (Class<Map<String, Object>>) (Class<?>) Map.class);
        this.eventPublisher = eventPublisher;
    }

    @Override public int getOrder() { return 20; }
    @Override public boolean isGlobal() { return true; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<Map<String, Object>> config, TaskDefinition taskDef) {
        return new ObservabilityInterceptor(eventPublisher);
    }
}
