package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.DataType;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Especialista em traduzir nós JSON de requires/produces para objetos DataSpec.
 * Suporta tanto a sintaxe curta (String) quanto a sintaxe longa (Objeto completo).
 * Java 21: Utiliza SequencedCollections e imutabilidade.
 */
public final class DataSpecParser {

    private DataSpecParser() {}

    /**
     * Converte um JsonNode (Array) em uma lista imutável de DataSpec.
     * Suporta ["campo1", {"name": "campo2", "path": "dados.valor"}]
     */
    public static List<DataSpec> parse(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Collections.emptyList();
        }

        List<DataSpec> specs = new ArrayList<>();
        node.forEach(item -> {
            if (item.isTextual()) {
                specs.add(DataSpec.of(item.asText()));
            } else if (item.isObject()) {
                specs.add(mapObjectToSpec(item));
            }
        });
        
        // Java 21: Retorna uma lista imutável para segurança de domínio
        return List.copyOf(specs);
    }

    private static DataSpec mapObjectToSpec(JsonNode item) {
        // Uso de path().asText() para evitar NPE se o campo não existir
        String name = item.path("name").asText();
        String path = item.has("path") ? item.get("path").asText() : null;
        boolean optional = item.path("optional").asBoolean(false);
        String typeStr = item.path("type").asText("ANY").toUpperCase();
        
        return new DataSpec(name, DataType.valueOf(typeStr), optional, path);
    }
}
