package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.api.task.AbstractInterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ERROR_HANDLER")
public class ErrorHandlerInterceptorProvider extends AbstractInterceptorProvider<Map<String, Object>> {

    public ErrorHandlerInterceptorProvider(TaskBindingResolver bindingResolver) {
        super(bindingResolver, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }

    @Override public int getOrder() { return 10; }
    @Override public boolean isGlobal() { return true; }

    @Override
    protected TaskInterceptor createInterceptor(CompiledConfiguration<Map<String, Object>> config, TaskDefinition taskDef) {
        return new ErrorHandlerInterceptor();
    }
}
