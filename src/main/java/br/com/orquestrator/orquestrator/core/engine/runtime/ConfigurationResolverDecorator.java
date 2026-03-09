package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * ConfigurationResolverDecorator: OBSOLETO.
 * A resolução de configuração agora é feita no startup ou pela própria Task.
 */
@RequiredArgsConstructor
public class ConfigurationResolverDecorator implements TaskInterceptor {

    private final TaskBindingResolver bindingResolver;
    private final Map<String, Object> rawConfig;
    private final Class<?> configClass;

    @Override
    public TaskResult intercept(Chain chain) {
        return chain.proceed(chain.inputs());
    }
}
