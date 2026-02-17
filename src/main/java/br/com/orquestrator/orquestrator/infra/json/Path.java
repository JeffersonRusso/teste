package br.com.orquestrator.orquestrator.infra.json;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstração de um caminho no documento (ex: "cliente.endereco.rua").
 * Encapsula a lógica de navegação, seguindo o SRP (Single Responsibility Principle).
 */
public record Path(String[] segments) {

    public static Path of(String path) {
        if (path == null || path.isBlank()) throw new IllegalArgumentException("Caminho não pode ser vazio");
        return new Path(path.split("\\."));
    }

    /**
     * Navega no mapa de forma declarativa usando reduce.
     */
    public Object read(Map<String, Object> root) {
        return Arrays.stream(segments)
                .reduce(root, this::navigate, (_, b) -> b);
    }

    /**
     * Escreve o valor criando a hierarquia necessária.
     */
    @SuppressWarnings("unchecked")
    public void write(Map<String, Object> root, Object value) {
        Map<String, Object> current = root;
        for (int i = 0; i < segments.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(segments[i], 
                _ -> new ConcurrentHashMap<String, Object>());
        }
        current.put(segments[segments.length - 1], value);
    }

    private Object navigate(Object node, String key) {
        return (node instanceof Map<?, ?> m) ? m.get(key) : null;
    }
}
