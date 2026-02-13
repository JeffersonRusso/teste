package br.com.orquestrator.orquestrator.tasks.interceptor.cache;

import java.util.Optional;

public interface CacheProvider {
    
    String getType(); // "IN_MEMORY", "REDIS", etc.
    
    Optional<Object> get(String cacheName, String key);
    
    void put(String cacheName, String key, Object value, long ttlMs);
}
