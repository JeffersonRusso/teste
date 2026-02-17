package br.com.orquestrator.orquestrator.infra.cache;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositório de Dados Globais: Simples e Direto.
 */
@Component
public class GlobalDataCache {

    private final Map<String, Object> storage = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        if (key != null && value != null) storage.put(key, value);
    }

    public Object get(String key) {
        return storage.get(key);
    }

    public Map<String, Object> getAll() {
        return storage;
    }
}
