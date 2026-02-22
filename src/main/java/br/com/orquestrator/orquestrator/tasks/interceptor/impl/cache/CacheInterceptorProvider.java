package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInterceptorProvider implements InterceptorProvider<CacheConfig> {

    private final CacheInterceptor interceptor;

    @Override
    public String featureType() {
        return "CACHE";
    }

    @Override
    public Class<CacheConfig> configClass() {
        return CacheConfig.class;
    }

    @Override
    public TaskInterceptor create(CacheConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
