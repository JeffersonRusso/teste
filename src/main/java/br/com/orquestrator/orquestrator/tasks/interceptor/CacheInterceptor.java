package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.cache.CacheProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interceptor responsável pela lógica de cache de resultados.
 */
@Slf4j
@Component("CACHE")
public class CacheInterceptor extends TypedTaskInterceptor<CacheConfig> {

    private final ExpressionService expressionService;
    private final Map<String, CacheProvider> providers;

    public CacheInterceptor(ExpressionService expressionService, List<CacheProvider> providerList) {
        super(CacheConfig.class);
        this.expressionService = expressionService;
        this.providers = providerList.stream()
                .collect(Collectors.toMap(CacheProvider::getType, p -> p));
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, CacheConfig config, TaskDefinition definition) {
        if (config == null || config.key() == null) {
            return next.proceed(context);
        }

        String nodeId = definition.getNodeId().value();
        CacheProvider provider = providers.getOrDefault(config.provider(), providers.get("IN_MEMORY"));

        try {
            EvaluationContext evalContext = expressionService.create(context);
            String cacheKey = evalContext.resolve(config.key(), String.class);

            context.track(nodeId, "cache.key", cacheKey);

            Optional<Object> cachedResult = provider.get(nodeId, cacheKey);

            if (cachedResult.isPresent()) {
                context.track(nodeId, "cache.hit", true);
                return TaskResult.success(cachedResult.get(), Map.of("cache_hit", true));
            }

            context.track(nodeId, "cache.hit", false);
            TaskResult result = next.proceed(context);

            if (result != null && result.body() != null) {
                provider.put(nodeId, cacheKey, result.body(), config.ttlMs());
            }
            return result;

        } catch (Exception e) {
            log.error(STR."Falha na operação de cache para \{nodeId}: \{e.getMessage()}");
            return next.proceed(context);
        }
    }
}
