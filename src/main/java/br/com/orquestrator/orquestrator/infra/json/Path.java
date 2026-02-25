package br.com.orquestrator.orquestrator.infra.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstração de um caminho no documento (ex: "cliente.endereco.rua").
 * Otimizado para ZERO contenção de lock e ZERO alocação de Regex.
 */
public record Path(String[] segments) {

    private static final Map<String, Path> CACHE = new ConcurrentHashMap<>(2048);

    public static Path of(String path) {
        if (path == null || path.isEmpty()) return null;
        
        // OTIMIZAÇÃO CRÍTICA: get() antes do computeIfAbsent()
        // O computeIfAbsent() bloqueia o bucket do mapa (synchronized).
        // O get() é lock-free. Em 99% dos casos, o caminho já está no cache.
        Path cached = CACHE.get(path);
        if (cached != null) return cached;

        // Só entramos no lock se o caminho for novo
        return CACHE.computeIfAbsent(path, p -> new Path(fastSplit(p)));
    }

    private static String[] fastSplit(String path) {
        int dotCount = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '.') dotCount++;
        }

        if (dotCount == 0) return new String[]{path.intern()};

        String[] result = new String[dotCount + 1];
        int start = 0;
        int currentResult = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '.') {
                result[currentResult++] = path.substring(start, i).intern();
                start = i + 1;
            }
        }
        result[currentResult] = path.substring(start).intern();
        return result;
    }

    public Object read(Map<String, Object> root) {
        if (root == null) return null;
        Object current = root;
        // Loop tradicional para evitar Iterator
        for (int i = 0; i < segments.length; i++) {
            if (current instanceof Map<?, ?> m) {
                current = m.get(segments[i]);
            } else {
                return null;
            }
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    public void write(Map<String, Object> root, Object value) {
        if (root == null) return;
        Map<String, Object> current = root;
        int lastIndex = segments.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            String segment = segments[i];
            Object next = current.get(segment);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                // Usamos ConcurrentHashMap para evitar contenção em escritas paralelas
                Map<String, Object> newNode = new ConcurrentHashMap<>();
                current.put(segment, newNode);
                current = newNode;
            }
        }
        current.put(segments[lastIndex], value);
    }
}
