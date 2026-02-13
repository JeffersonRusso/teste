package br.com.orquestrator.orquestrator.infra.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalDataCache {

    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "global_data";

    public void put(String key, Object value) {
        log.debug("[GlobalCache] PUT key={} value={}", key, value);
        getCache().put(key, value);
    }

    public Object get(String key) {
        Cache.ValueWrapper wrapper = getCache().get(key);
        Object value = wrapper != null ? wrapper.get() : null;
        if (value == null) {
            log.debug("[GlobalCache] GET key={} -> MISS", key);
        } else {
            log.debug("[GlobalCache] GET key={} -> HIT", key);
        }
        return value;
    }

    private Cache getCache() {
        return cacheManager.getCache(CACHE_NAME);
    }
}
