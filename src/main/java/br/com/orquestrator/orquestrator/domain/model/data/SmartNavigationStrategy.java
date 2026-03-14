package br.com.orquestrator.orquestrator.domain.model.data;

/**
 * SmartNavigationStrategy: Implementação padrão que distingue entre JSON Pointer e chaves simples.
 */
public class SmartNavigationStrategy implements NavigationStrategy {

    @Override
    public DataNode navigate(DataNode root, String path) {
        if (path == null || path.isBlank()) return root;

        // Se o caminho começa com '/', usa a navegação profunda (at).
        // Caso contrário, usa o acesso direto (get).
        return path.startsWith("/") ? root.at(path) : root.get(path);
    }
}
