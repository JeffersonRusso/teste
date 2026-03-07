package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.core.engine.runtime.CacheEngine;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheDecoratorFactory implements DecoratorFactory<CacheConfig> {

    private final ExpressionEngine expressionEngine;
    private final CacheEngine cacheEngine;

    @Override public String getType() { return "CACHE"; }
    @Override public Class<CacheConfig> getConfigClass() { return CacheConfig.class; }

    @Override
    public TaskInterceptor create(CacheConfig config, String nodeId) {
        return new CacheInterceptor(expressionEngine, cacheEngine, config, nodeId);
    }
}
