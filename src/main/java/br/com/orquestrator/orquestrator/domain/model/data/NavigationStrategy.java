package br.com.orquestrator.orquestrator.domain.model.data;

/**
 * NavigationStrategy: Define como navegar em uma estrutura de dados estruturada.
 */
public interface NavigationStrategy {
    
    /**
     * Resolve o nó de dados baseado em um caminho.
     */
    DataNode navigate(DataNode root, String path);
}
