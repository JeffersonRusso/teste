package br.com.orquestrator.orquestrator.tasks.interceptor.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryCacheProvider implements CacheProvider {

    private final Map<String, Cache<String, Object>> caches = new ConcurrentHashMap<>();

    @Override
    public String getType() {
        return "IN_MEMORY";
    }

    @Override
    public Optional<Object> get(String cacheName, String key) {
        // O TTL é definido na criação, então aqui assumimos um default se não existir
        Cache<String, Object> cache = caches.get(cacheName);
        return cache != null ? Optional.ofNullable(cache.getIfPresent(key)) : Optional.empty();
    }

    @Override
    public void put(String cacheName, String key, Object value, long ttlMs) {
        Cache<String, Object> cache = caches.computeIfAbsent(cacheName, k -> 
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMillis(ttlMs))
                    .maximumSize(1000)
                    .build()
        );
        cache.put(key, value);
    }
}
