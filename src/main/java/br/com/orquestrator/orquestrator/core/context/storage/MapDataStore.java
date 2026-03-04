package br.com.orquestrator.orquestrator.core.context.storage;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MapDataStore: Implementação nativa de um banco de dados hierárquico.
 * Resolve o erro de navegação (EL1008E) ao gerenciar a árvore de mapas internamente.
 */
public class MapDataStore implements DataStore, ReadableContext {

    private final Map<String, Object> storage = new ConcurrentHashMap<>(64);

    @Override
    @SuppressWarnings("unchecked")
    public void put(String key, Object value) {
        if (key == null || key.isBlank()) return;

        // Se não há pontos, é uma gravação direta na raiz
        if (key.indexOf('.') == -1) {
            storage.put(key, value);
            return;
        }

        // Navegação hierárquica nativa (O Coração da Solução)
        String[] parts = key.split("\\.");
        Map<String, Object> current = storage;

        for (int i = 0; i < parts.length - 1; i++) {
            // Cria o mapa intermediário se não existir, garantindo a estrutura para o SpEL
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new ConcurrentHashMap<>());
        }
        current.put(parts[parts.length - 1], value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String key) {
        if (key == null || key.isBlank()) return null;

        if (key.indexOf('.') == -1) {
            return storage.get(key);
        }

        String[] parts = key.split("\\.");
        Object current = storage;

        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<String, Object>) current).get(part);
            if (current == null) return null;
        }
        return current;
    }

    @Override public Map<String, Object> getRoot() { return storage; }
    @Override public Map<String, Object> getAll() { return storage; }
    @Override public boolean contains(String key) { return get(key) != null; }
}
