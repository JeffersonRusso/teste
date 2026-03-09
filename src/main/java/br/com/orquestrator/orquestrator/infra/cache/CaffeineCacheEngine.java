package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.core.engine.runtime.CacheEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaffeineCacheEngine implements CacheEngine {

    private final Map<String, Cache<String, JsonNode>> caches = new ConcurrentHashMap<>();

    @Override
    public Optional<JsonNode> get(String namespace, String key) {
        Cache<String, JsonNode> cache = caches.get(namespace);
        return (cache != null) ? Optional.ofNullable(cache.getIfPresent(key)) : Optional.empty();
    }

    @Override
    public void put(String namespace, String key, JsonNode value, long ttlMs) {
        Cache<String, JsonNode> cache = caches.computeIfAbsent(namespace, k -> 
            CacheFactory.createVolatileCache(10000, ttlMs > 0 ? ttlMs / 1000 : 300)
        );
        cache.put(key, value);
    }
}
