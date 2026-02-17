package br.com.orquestrator.orquestrator.infra.json;

import java.util.List;
import java.util.Map;

/**
 * Extrator Universal: A única forma de navegar em dados no sistema.
 * Opera apenas sobre tipos Java nativos (Map/List).
 */
public final class MapExtractor {
    
    public static Object extract(Object root, String path) {
        if (root == null || path == null || path.isBlank()) return root;
        
        Object current = root;
        String[] segments = path.split("\\.");
        
        for (String segment : segments) {
            if (current instanceof Map<?, ?> m) {
                current = m.get(segment);
            } else if (current instanceof List<?> l) {
                try {
                    int index = Integer.parseInt(segment);
                    current = (index >= 0 && index < l.size()) ? l.get(index) : null;
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
            if (current == null) return null;
        }
        return current;
    }
}
