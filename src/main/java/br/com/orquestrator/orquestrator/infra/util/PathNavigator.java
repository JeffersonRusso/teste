package br.com.orquestrator.orquestrator.infra.util;

import java.util.Map;

/**
 * PathNavigator: Navegação em mapas aninhados otimizada para alta performance.
 * Evita split() e alocação de memória desnecessária.
 */
public class PathNavigator {

    public static Object find(Map<String, Object> data, String path) {
        if (data == null || path == null) return null;

        Object current = data;
        int start = 0;
        int dotIdx;

        while ((dotIdx = path.indexOf('.', start)) != -1) {
            if (!(current instanceof Map)) return null;
            
            String part = path.substring(start, dotIdx);
            current = ((Map<?, ?>) current).get(part);
            start = dotIdx + 1;
            
            if (current == null) return null;
        }

        // Pega a última parte
        if (current instanceof Map) {
            return ((Map<?, ?>) current).get(path.substring(start));
        }

        return null;
    }
}
