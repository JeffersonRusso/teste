package br.com.orquestrator.orquestrator.infra.json;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para construção de mapas aninhados a partir de chaves com ponto.
 * Ex: "cliente.endereco.rua" -> { "cliente": { "endereco": { "rua": "valor" } } }
 */
@Component
public class MapBuilder {

    @SuppressWarnings("unchecked")
    public void addNested(Map<String, Object> map, String path, Object value) {
        if (path == null || path.isEmpty()) return;

        Map<String, Object> current = map;
        int start = 0;
        int end;

        // Loop otimizado sem Regex e sem criar array de strings
        while ((end = path.indexOf('.', start)) != -1) {
            String key = path.substring(start, end);
            current = (Map<String, Object>) current.computeIfAbsent(key, k -> new HashMap<>());
            start = end + 1;
        }

        // Última parte
        String lastKey = path.substring(start);
        current.put(lastKey, value);
    }
}
