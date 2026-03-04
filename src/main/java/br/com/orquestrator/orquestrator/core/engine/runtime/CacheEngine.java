package br.com.orquestrator.orquestrator.core.engine.runtime;

import java.util.Optional;

/**
 * CacheEngine: O único ponto de contato para persistência temporária de dados.
 * Abstrai o provedor real (Caffeine, Redis, etc).
 */
public interface CacheEngine {
    
    /** Recupera um valor do cache. */
    Optional<Object> get(String namespace, String key);
    
    /** Grava um valor no cache com tempo de vida. */
    void put(String namespace, String key, Object value, long ttlMs);
    
    /** Invalida uma entrada ou um namespace inteiro. */
    void evict(String namespace, String key);
}
