package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.runtime.CacheEngine;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CacheInterceptor implements TaskInterceptor {

    private final ExpressionEngine expressionEngine;
    private final CacheEngine cacheEngine;
    private final CacheConfig config;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        if (config == null || config.key() == null) return chain.proceed(chain.context());

        try {
            DataValue keyDv = expressionEngine.compile(config.key()).evaluate(ContextHolder.reader());
            String cacheKey = keyDv.as(String.class).orElse(keyDv.raw().toString());

            Optional<DataValue> cached = cacheEngine.get(nodeId, cacheKey);

            if (cached.isPresent()) {
                log.debug("Cache HIT [{}] key [{}]", nodeId, cacheKey);
                return TaskResult.success(cached.get(), Map.of("cache_hit", true));
            }

            TaskResult result = chain.proceed(chain.context());
            if (result != null && result.isSuccess()) {
                cacheEngine.put(nodeId, cacheKey, result.body(), config.ttlMs());
            }
            return result;

        } catch (Exception e) {
            log.error("Falha na operação de cache para {}: {}", nodeId, e.getMessage());
            return chain.proceed(chain.context());
        }
    }
}
