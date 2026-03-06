package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.core.engine.runtime.CacheEngine;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class CaffeineCacheEngine implements CacheEngine {

    private final Map<String, Cache<String, DataValue>> caches = new ConcurrentHashMap<>();

    @Override
    public Optional<DataValue> get(String namespace, String key) {
        Cache<String, DataValue> cache = caches.get(namespace);
        return cache != null ? Optional.ofNullable(cache.getIfPresent(key)) : Optional.empty();
    }

    @Override
    public void put(String namespace, String key, DataValue value, long ttlMs) {
        Cache<String, DataValue> cache = caches.computeIfAbsent(namespace, k -> 
            Caffeine.newBuilder()
                .expireAfterWrite(ttlMs, TimeUnit.MILLISECONDS)
                .maximumSize(10_000)
                .build()
        );
        cache.put(key, value);
    }

    @Override
    public void evict(String namespace, String key) {
        Cache<String, DataValue> cache = caches.get(namespace);
        if (cache != null) {
            if (key == null) cache.invalidateAll();
            else cache.invalidate(key);
        }
    }
}
