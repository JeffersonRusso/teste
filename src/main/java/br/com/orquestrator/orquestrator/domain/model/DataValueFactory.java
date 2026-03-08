package br.com.orquestrator.orquestrator.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataValueFactory: A "Alfândega" do sistema.
 * Único lugar autorizado a adivinhar tipos e transformá-los em DataValues.
 */
public final class DataValueFactory {

    public static DataValue of(Object val) {
        return of(val, null);
    }

    @SuppressWarnings("unchecked")
    public static DataValue of(Object val, String semanticType) {
        if (val == null) return DataValue.EMPTY;
        if (val instanceof DataValue dv) return dv;
        
        return switch (val) {
            case String s -> new DataValue.Text(s, semanticType);
            case Number n -> new DataValue.Numeric(n, semanticType);
            case Boolean b -> (semanticType == null) ? (b ? DataValue.TRUE : DataValue.FALSE) : new DataValue.Logic(b, semanticType);
            case Map m -> fromMap((Map<String, ?>) m, semanticType);
            case List l -> fromList((List<?>) l, semanticType);
            case JsonNode node -> fromJsonNode(node, semanticType);
            default -> new DataValue.DomainObject(val, semanticType);
        };
    }

    private static DataValue fromMap(Map<String, ?> map, String semanticType) {
        Map<String, DataValue> converted = new HashMap<>();
        map.forEach((k, v) -> converted.put(k, of(v)));
        return new DataValue.Mapping(converted, semanticType);
    }

    private static DataValue fromList(List<?> list, String semanticType) {
        List<DataValue> converted = new ArrayList<>();
        list.forEach(v -> converted.add(of(v)));
        return new DataValue.Sequence(converted, semanticType);
    }

    public static DataValue fromJsonNode(JsonNode node, String semanticType) {
        if (node == null || node.isNull() || node.isMissingNode()) return DataValue.EMPTY;
        
        // Se for um objeto ou array complexo, usamos o record especializado JsonValue
        if (node.isObject() || node.isArray()) {
            return new DataValue.JsonValue(node, semanticType);
        }

        // Se for um valor escalar, convertemos para o tipo nativo correspondente
        if (node.isTextual()) return new DataValue.Text(node.asText(), semanticType);
        if (node.isNumber()) return new DataValue.Numeric(node.numberValue(), semanticType);
        if (node.isBoolean()) return (semanticType == null) ? (node.asBoolean() ? DataValue.TRUE : DataValue.FALSE) : new DataValue.Logic(node.asBoolean(), semanticType);

        return new DataValue.DomainObject(node, semanticType);
    }
}
