package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.cache.CacheProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CacheInterceptor implements TaskDecorator {

    private final ExpressionService expressionService;
    private final CacheProvider provider;
    private final CacheConfig config;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskChain next) {
        if (config == null || config.key() == null) {
            return next.proceed();
        }

        try {
            String cacheKey = expressionService.resolve(config.key(), String.class);
            Optional<Object> cachedResult = provider.get(nodeId, cacheKey);

            if (cachedResult.isPresent()) {
                log.debug("Cache HIT para nó [{}] chave [{}]", nodeId, cacheKey);
                return TaskResult.success(cachedResult.get(), Map.of("cache_hit", true));
            }

            log.debug("Cache MISS para nó [{}] chave [{}]", nodeId, cacheKey);
            TaskResult result = next.proceed();

            if (result != null && result.isSuccess() && result.body() != null) {
                provider.put(nodeId, cacheKey, result.body().raw(), config.ttlMs());
            }
            return result;

        } catch (Exception e) {
            log.error("Falha na operação de cache para {}: {}", nodeId, e.getMessage());
            return next.proceed();
        }
    }
}
