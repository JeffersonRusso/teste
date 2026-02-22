package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FallbackInterceptorProvider implements InterceptorProvider<FallbackConfig> {

    private final FallbackInterceptor interceptor;

    @Override
    public String featureType() {
        return "FALLBACK";
    }

    @Override
    public Class<FallbackConfig> configClass() {
        return FallbackConfig.class;
    }

    @Override
    public TaskInterceptor create(FallbackConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
