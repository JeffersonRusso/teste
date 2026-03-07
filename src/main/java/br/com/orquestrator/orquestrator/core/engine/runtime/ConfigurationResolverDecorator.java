package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ConfigurationResolverDecorator implements TaskInterceptor {

    private final TaskBindingResolver bindingResolver;
    private final Map<String, Object> rawConfig;
    private final Class<?> configClass;

    @Override
    public TaskResult intercept(Chain chain) {
        Object resolvedConfig = bindingResolver.resolve(rawConfig, configClass);
        
        TaskContext enrichedContext = new TaskContext(
            chain.context().inputs(), 
            DataValue.of(resolvedConfig), 
            chain.context().nodeId(), 
            chain.context().requiredFields()
        );
        
        return chain.proceed(enrichedContext);
    }
}
