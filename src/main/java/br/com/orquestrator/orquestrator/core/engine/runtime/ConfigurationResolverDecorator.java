package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * ConfigurationResolverDecorator: Resolve expressões dinâmicas na configuração da Task.
 * Agora usa o Shadow Context (inputs) para a resolução.
 */
@RequiredArgsConstructor
public class ConfigurationResolverDecorator implements TaskInterceptor {

    private final TaskBindingResolver bindingResolver;
    private final Map<String, Object> rawConfig;
    private final Class<?> configClass;

    @Override
    public TaskResult intercept(Chain chain) {
        // Resolve a configuração usando os inputs já coletados pelo nó
        Object resolvedConfig = bindingResolver.resolve(rawConfig, chain.context().inputs(), configClass);
        
        TaskContext enrichedContext = new TaskContext(
            chain.context().inputs(), 
            DataValueFactory.of(resolvedConfig),
            chain.context().nodeId(), 
            chain.context().requiredFields()
        );
        
        return chain.proceed(enrichedContext);
    }
}
