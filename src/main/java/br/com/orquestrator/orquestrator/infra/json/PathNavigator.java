package br.com.orquestrator.orquestrator.infra.json;

import java.util.Map;

/**
 * Fachada para navegação de caminhos.
 * Delega a inteligência para a abstração Path.
 */
public final class PathNavigator {

    private PathNavigator() {}

    public static void write(Map<String, Object> root, String path, Object value) {
        if (path == null || value == null) return;
        Path.of(path).write(root, value);
    }

    public static Object read(Map<String, Object> root, String path) {
        if (path == null) return null;
        return Path.of(path).read(root);
    }
}
