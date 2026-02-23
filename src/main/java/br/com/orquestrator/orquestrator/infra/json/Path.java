package br.com.orquestrator.orquestrator.infra.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstração de um caminho no documento (ex: "cliente.endereco.rua").
 * Otimizado com cache de segmentos para evitar split() e GC excessivo no hot path.
 */
public record Path(String[] segments) {

    private static final Map<String, Path> CACHE = new ConcurrentHashMap<>(1024);

    public static Path of(String path) {
        if (path == null || path.isBlank()) throw new IllegalArgumentException("Caminho não pode ser vazio");
        return CACHE.computeIfAbsent(path, p -> new Path(p.split("\\.")));
    }

    /**
     * Navega no mapa de forma imperativa para performance máxima (evita overhead de Stream/Lambda).
     */
    public Object read(Map<String, Object> root) {
        Object current = root;
        for (String segment : segments) {
            if (current instanceof Map<?, ?> m) {
                current = m.get(segment);
            } else {
                return null;
            }
        }
        return current;
    }

    /**
     * Escreve o valor criando a hierarquia necessária.
     * Otimizado para evitar lambdas no loop interno.
     */
    @SuppressWarnings("unchecked")
    public void write(Map<String, Object> root, Object value) {
        Map<String, Object> current = root;
        for (int i = 0; i < segments.length - 1; i++) {
            String segment = segments[i];
            Object next = current.get(segment);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                Map<String, Object> newNode = new ConcurrentHashMap<>();
                current.put(segment, newNode);
                current = newNode;
            }
        }
        current.put(segments[segments.length - 1], value);
    }
}
