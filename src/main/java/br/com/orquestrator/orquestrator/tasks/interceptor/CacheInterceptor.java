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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interceptor responsável pela lógica de cache de resultados.
 * Java 21: Utiliza String Templates e Pattern Matching para manipulação de dados.
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
    protected void interceptTyped(TaskData data, TaskChain next, CacheConfig config, TaskDefinition definition) {
        if (config == null || config.key() == null) {
            next.proceed(data);
            return;
        }

        String cacheName = definition.getNodeId().value();
        CacheProvider provider = providers.getOrDefault(config.provider(), providers.get("IN_MEMORY"));

        try {
            // 1. Resolução da Chave (Fast Path)
            EvaluationContext evalContext = expressionService.create(data);
            String cacheKey = evalContext.resolve(config.key(), String.class);

            data.addMetadata("cache.key", cacheKey);

            // 2. Tentativa de Recuperação
            Optional<Object> cachedResult = provider.get(cacheName, cacheKey);

            if (cachedResult.isPresent()) {
                data.addMetadata("cache.hit", true);
                applyCachedData(data, cachedResult.get(), definition);
                return;
            }

            // 3. Cache Miss: Executa a Task
            data.addMetadata("cache.hit", false);
            next.proceed(data);

            // 4. Armazenamento Assíncrono/Silencioso
            storeInCache(data, definition, provider, cacheName, cacheKey, config.ttlMs());

        } catch (Exception e) {
            // Se o cache falhar (infra), o pipeline DEVE continuar
            log.error(STR."Falha na operação de cache para \{cacheName}: \{e.getMessage()}");
            
            // Java 21: Verificação de metadado usando getMetadata e comparação nula
            if (data.getMetadata("cache.hit") == null) {
                next.proceed(data);
            }
        }
    }

    private void applyCachedData(TaskData data, Object result, TaskDefinition definition) {
        List<DataSpec> produces = definition.getProduces();
        if (produces == null || produces.isEmpty()) return;

        // Java 21 Pattern Matching para extração de Mapas
        if (result instanceof Map<?, ?> mapResult) {
            produces.forEach(spec -> {
                Object val = mapResult.get(spec.name());
                if (val != null) data.put(spec.name(), val);
            });
        } else if (produces.size() == 1) {
            data.put(produces.getFirst().name(), result);
        }
    }

    private void storeInCache(TaskData data, TaskDefinition def, CacheProvider provider, String name, String key, long ttl) {
        List<DataSpec> produces = def.getProduces();
        if (produces == null || produces.isEmpty()) return;

        // Construção eficiente do payload de cache usando Stream API
        Object resultToCache = produces.size() == 1
                ? data.get(produces.getFirst().name())
                : produces.stream()
                .filter(spec -> data.get(spec.name()) != null)
                .collect(Collectors.toMap(DataSpec::name, spec -> data.get(spec.name())));

        if (resultToCache != null) {
            provider.put(name, key, resultToCache, ttl);
        }
    }
}
