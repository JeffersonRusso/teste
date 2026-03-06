package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Optional;

/**
 * CacheEngine: O único ponto de contato para persistência temporária de DataValues.
 */
public interface CacheEngine {
    
    /** Recupera um DataValue do cache. */
    Optional<DataValue> get(String namespace, String key);
    
    /** Grava um DataValue no cache com tempo de vida. */
    void put(String namespace, String key, DataValue value, long ttlMs);
    
    void evict(String namespace, String key);
}
