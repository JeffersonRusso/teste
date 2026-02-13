package br.com.orquestrator.orquestrator.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

/**
 * Wrapper que abstrai a origem do dado (JsonNode, Map, Primitivo).
 * Elimina a necessidade de 'instanceof' e casts manuais no código de negócio.
 */
public record DataValue(Object rawValue) {

    public static DataValue of(Object value) {
        return new DataValue(value);
    }

    public boolean isPresent() {
        return rawValue != null;
    }

    public String asString() {
        if (rawValue instanceof JsonNode node) return node.asText();
        return rawValue != null ? rawValue.toString() : null;
    }

    public Integer asInt() {
        if (rawValue instanceof JsonNode node) return node.asInt();
        if (rawValue instanceof Number num) return num.intValue();
        return isPresent() ? Integer.parseInt(asString()) : null;
    }

    public Double asDouble() {
        if (rawValue instanceof JsonNode node) return node.asDouble();
        if (rawValue instanceof Number num) return num.doubleValue();
        return isPresent() ? Double.parseDouble(asString()) : null;
    }

    public boolean asBoolean() {
        if (rawValue instanceof JsonNode node) return node.asBoolean();
        if (rawValue instanceof Boolean bool) return bool;
        return Boolean.parseBoolean(asString());
    }

    /**
     * Retorna o objeto original para casos onde a engine externa (SpEL/DMN) 
     * precisa do tipo nativo.
     */
    public Object unwrap() {
        return rawValue;
    }
}
