package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CacheConfig(
    String key,
    long ttlMs,
    String provider
) {
}
