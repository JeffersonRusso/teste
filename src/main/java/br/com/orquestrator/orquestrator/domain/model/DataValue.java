package br.com.orquestrator.orquestrator.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DataValue: O átomo de dado soberano.
 * Agora polimórfico: cada tipo sabe como navegar em si mesmo.
 */
public sealed interface DataValue 
    permits DataValue.Text, DataValue.Numeric, DataValue.Logic, 
            DataValue.Mapping, DataValue.Sequence, DataValue.JsonValue,
            DataValue.DomainObject, DataValue.Empty {
    
    DataValue EMPTY = new Empty();
    DataValue TRUE = new Logic(true, null);
    DataValue FALSE = new Logic(false, null);

    String semanticType();
    Object raw();

    /** Contrato de Navegação: Mata o 'instanceof' externo. */
    default DataValue get(String key) {
        return EMPTY; // Comportamento padrão para folhas (Text, Numeric, etc)
    }

    record Text(String value, String semanticType) implements DataValue {
        @Override public Object raw() { return value; }
    }
    
    record Numeric(Number value, String semanticType) implements DataValue, Comparable<Number> {
        @Override public Object raw() { return value; }
        public double doubleValue() { return value.doubleValue(); }
        @Override public int compareTo(Number other) { return Double.compare(this.doubleValue(), other.doubleValue()); }
    }
    
    record Logic(Boolean value, String semanticType) implements DataValue {
        @Override public Object raw() { return value; }
    }

    record Mapping(Map<String, DataValue> fields, String semanticType) implements DataValue {
        public Mapping { fields = Collections.unmodifiableMap(fields); }
        @Override public Object raw() { return fields; }
        @Override public DataValue get(String key) {
            return fields.getOrDefault(key, EMPTY);
        }
    }

    record Sequence(List<DataValue> items, String semanticType) implements DataValue {
        public Sequence { items = Collections.unmodifiableList(items); }
        @Override public Object raw() { return items; }
        // Futuro: suportar get por índice (ex: "0", "1")
    }

    record JsonValue(JsonNode node, String semanticType) implements DataValue {
        @Override public Object raw() { return node; }
        @Override public DataValue get(String key) {
            JsonNode child = node.get(key);
            return (child == null || child.isMissingNode()) ? EMPTY : DataValueFactory.fromJsonNode(child, null);
        }
    }

    record DomainObject(Object instance, String semanticType) implements DataValue {
        @Override public Object raw() { return instance; }
        @Override public DataValue get(String key) {
            if (instance instanceof Map<?, ?> m) {
                return DataValueFactory.of(m.get(key));
            }
            return EMPTY;
        }
    }

    record Empty() implements DataValue {
        @Override public String semanticType() { return null; }
        @Override public Object raw() { return null; }
    }

    default <T> Optional<T> as(Class<T> type) {
        Object r = raw();
        return (type.isInstance(r)) ? Optional.of(type.cast(r)) : Optional.empty();
    }

    default boolean isEmpty() { return this instanceof Empty; }
}
