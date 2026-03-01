package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.cache.CacheProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheDecoratorFactory implements DecoratorFactory<CacheConfig> {

    private final ExpressionService expressionService;
    private final List<CacheProvider> cacheProviders;

    @Override
    public String getType() {
        return "CACHE";
    }

    @Override
    public Class<CacheConfig> getConfigClass() {
        return CacheConfig.class;
    }

    @Override
    public TaskDecorator create(CacheConfig config, String nodeId) {
        CacheProvider provider = cacheProviders.stream()
                .filter(p -> p.getType().equalsIgnoreCase(config.provider()))
                .findFirst()
                .orElse(cacheProviders.get(0));

        return new CacheInterceptor(expressionService, provider, config, nodeId);
    }
}
