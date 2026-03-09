package br.com.orquestrator.orquestrator.infra.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * CacheFactory: Fábrica centralizada para caches de alta performance (Caffeine).
 * Garante configurações consistentes para o caminho quente.
 */
public final class CacheFactory {

    /**
     * Cria um cache eterno para objetos estruturais (ex: DataPath, Jackson Readers).
     * Ideal para dados que são finitos e repetitivos.
     */
    public static <K, V> Cache<K, V> createHotCache(int initialCapacity) {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(10_000) // Proteção contra memory leak
                .recordStats() // Habilita métricas para JMX/Micrometer
                .build();
    }

    /**
     * Cria um cache com expiração para dados dinâmicos.
     */
    public static <K, V> Cache<K, V> createVolatileCache(int size, long ttlSeconds) {
        return Caffeine.newBuilder()
                .maximumSize(size)
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .recordStats()
                .build();
    }
}
