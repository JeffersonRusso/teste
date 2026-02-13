package br.com.orquestrator.orquestrator.infra.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utilitário centralizado para manipulação e navegação de caminhos (dot notation).
 */
public final class PathSupport {

    private static final String SEPARATOR = ".";

    private PathSupport() {}

    public static boolean isNested(String path) {
        return path != null && path.contains(SEPARATOR);
    }

    public static String getRoot(String path) {
        if (!isNested(path)) return path;
        return path.substring(0, path.indexOf(SEPARATOR));
    }

    /**
     * Gera a hierarquia completa de um caminho.
     * Ex: "a.b.c" -> ["a", "a.b", "a.b.c"]
     */
    public static Stream<String> getHierarchy(String path) {
        if (path == null || path.isBlank()) return Stream.empty();
        
        List<String> levels = new ArrayList<>();
        String current = path;
        levels.add(current);
        
        while (isNested(current)) {
            current = current.substring(0, current.lastIndexOf(SEPARATOR));
            levels.add(current);
        }
        return levels.stream();
    }

    /**
     * Navega recursivamente em um objeto para extrair o valor de um caminho.
     */
    public static Object extract(Object root, String path) {
        if (root == null || path == null) return null;
        if (!isNested(path)) return navigate(root, path);

        Object current = root;
        for (String part : path.split("\\.")) {
            current = navigate(current, part);
            if (current == null) break;
        }
        return current;
    }

    private static Object navigate(Object target, String property) {
        return switch (target) {
            case Map<?, ?> map -> map.get(property);
            case JsonNode node -> {
                JsonNode child = node.path(property);
                yield child.isMissingNode() ? null : child;
            }
            case null, default -> null;
        };
    }
}
