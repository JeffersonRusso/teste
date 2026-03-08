package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.vo.DataPath;

/**
 * DataValueNavigator: Inteligência de navegação em grafos de dados.
 * Garante que caminhos como "perfil.id" funcionem em Maps, JSONs e Mappings.
 */
public final class DataValueNavigator {

    public static DataValue navigate(DataValue source, String pathStr) {
        if (pathStr == null || pathStr.isEmpty() || ".".equals(pathStr)) return source;
        return navigate(source, DataPath.of(pathStr));
    }

    public static DataValue navigate(DataValue source, DataPath path) {
        if (path == null || path.isEmpty()) return source;
        if (source == null || source.isEmpty()) return DataValue.EMPTY;
        
        DataValue current = source;
        String[] parts = path.getParts();
        
        for (String part : parts) {
            current = current.get(part);
            if (current.isEmpty()) return DataValue.EMPTY;
        }
        
        return current;
    }
}
