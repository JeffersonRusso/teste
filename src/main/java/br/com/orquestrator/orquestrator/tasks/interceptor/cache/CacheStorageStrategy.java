package br.com.orquestrator.orquestrator.tasks.interceptor.cache;

import java.util.Optional;

public interface CacheStorageStrategy {
    
    String getType(); // "CAFFEINE", "REDIS", etc.
    
    Optional<Object> get(String taskId, String key);
    
    void put(String taskId, String key, Object value, long ttlMs);
}
