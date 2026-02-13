package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.cache.CacheProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    protected void interceptTyped(TaskData data, TaskChain next, CacheConfig config, TaskDefinition definition) {
        if (config == null || config.key() == null) {
            next.proceed(data);
            return;
        }

        CacheProvider provider = providers.getOrDefault(config.provider(), providers.get("IN_MEMORY"));
        String cacheName = definition.getNodeId().value();
        
        // Nota: Aqui ainda precisamos do contexto original para resolver a chave do cache.
        // Como o TaskData é uma ContractView, podemos acessar o contexto se necessário,
        // mas por simplicidade, vamos assumir que a chave pode ser resolvida com o que a task tem.
        // Se falhar, precisaremos de um método no TaskData para expor o contexto de avaliação.
        EvaluationContext evalContext = expressionService.create(data); 
        String cacheKey = evalContext.resolve(config.key(), String.class);
        
        data.addMetadata("cache.key", cacheKey);
        data.addMetadata("cache.provider", provider.getType());

        Optional<Object> cachedResult = provider.get(cacheName, cacheKey);
        
        if (cachedResult.isPresent()) {
            data.addMetadata("cache.hit", true);
            mapCachedResultToContext(data, cachedResult.get(), definition);
            return;
        }

        data.addMetadata("cache.hit", false);
        next.proceed(data);
        
        Object resultToCache = extractResultToCache(data, definition);
        if (resultToCache != null) {
            provider.put(cacheName, cacheKey, resultToCache, config.ttlMs());
        }
    }

    private void mapCachedResultToContext(TaskData data, Object result, TaskDefinition definition) {
        List<DataSpec> produces = definition.getProduces();
        if (produces == null || produces.isEmpty()) return;

        if (result instanceof Map<?, ?> mapResult) {
             for (int i = 0; i < produces.size(); i++) {
                 String name = produces.get(i).name();
                 Object value = mapResult.get(name);
                 data.put(name, value != null ? value : result);
             }
        } else {
            data.put(produces.getFirst().name(), result);
        }
    }

    private Object extractResultToCache(TaskData data, TaskDefinition definition) {
        List<DataSpec> produces = definition.getProduces();
        if (produces == null || produces.isEmpty()) return null;

        if (produces.size() == 1) {
            return data.get(produces.getFirst().name());
        }

        Map<String, Object> combined = new HashMap<>();
        for (int i = 0; i < produces.size(); i++) {
            String name = produces.get(i).name();
            Object val = data.get(name);
            if (val != null) combined.put(name, val);
        }
        return combined.isEmpty() ? null : combined;
    }
}
