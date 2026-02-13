package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Path;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Motor de busca de dados no contexto.
 * Centraliza a lógica de navegação em Maps e JsonNodes.
 * Java 21: Utiliza Sequenced Collections, Switch Expressions e Pattern Matching.
 */
public final class PathResolver {

    private PathResolver() {}

    /**
     * Resolve um caminho de dados (simples ou aninhado) dentro do contexto.
     */
    public static DataValue resolve(ExecutionContext context, String pathValue) {
        // 1. Fast Path: Tenta acesso direto ao root (Otimização de performance)
        Object rootValue = context.get(pathValue);
        if (rootValue != null) return DataValue.of(rootValue);

        // 2. Java 21: Uso de Record Pattern para extrair o valor do Path
        Path path = (Path) Path.of(pathValue);
        String value = path.value();
        if (!value.contains(".")) {
            return DataValue.of(null);
        }

        // 3. Java 21: Uso de Sequenced Collections para iterar sobre os segmentos
        List<String> segments = List.of(pathValue.split("\\."));
        Object current = context.get(segments.getFirst());

        // 4. Navegação profunda com Switch Expression e Pattern Matching (incluindo null case)
        for (String segment : segments.subList(1, segments.size())) {
            current = switch (current) {
                case Map<?, ?> m -> m.get(segment);
                case JsonNode j -> {
                    JsonNode node = j.path(segment);
                    yield node.isMissingNode() ? null : node;
                }
                case null, default -> null;
            };
            
            if (current == null) break;
        }

        return DataValue.of(current);
    }
}
