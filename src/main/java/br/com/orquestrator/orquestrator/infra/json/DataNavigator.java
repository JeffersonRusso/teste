package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/**
 * Utilitário para navegação em estruturas de dados híbridas (Map/JsonNode).
 */
public final class DataNavigator {

    public static Object resolve(Object root, String path) {
        if (root == null || path == null || path.isBlank()) return root;
        
        Object current = root;
        String[] segments = path.split("\\.");
        
        for (String segment : segments) {
            if (current instanceof Map<?, ?> m) {
                current = m.get(segment);
            } else if (current instanceof JsonNode j) {
                JsonNode node = j.path(segment);
                current = node.isMissingNode() ? null : node;
            } else {
                return null;
            }
            if (current == null) return null;
        }
        return current;
    }
}
