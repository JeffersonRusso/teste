package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.runtime.CacheEngine;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CacheInterceptor implements TaskDecorator {

    private final ExpressionEngine expressionEngine;
    private final CacheEngine cacheEngine; // <--- Abstração Soberana
    private final CacheConfig config;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskChain next) {
        if (config == null || config.key() == null) return next.proceed();

        try {
            String cacheKey = expressionEngine.evaluate(config.key(), ContextHolder.reader(), String.class);
            Optional<Object> cached = cacheEngine.get(nodeId, cacheKey);

            if (cached.isPresent()) {
                log.debug("Cache HIT [{}] key [{}]", nodeId, cacheKey);
                return TaskResult.success(cached.get(), Map.of("cache_hit", true));
            }

            TaskResult result = next.proceed();
            if (result != null && result.isSuccess() && result.body() != null) {
                cacheEngine.put(nodeId, cacheKey, result.body().raw(), config.ttlMs());
            }
            return result;

        } catch (Exception e) {
            log.error("Falha na operação de cache para {}: {}", nodeId, e.getMessage());
            return next.proceed();
        }
    }
}
