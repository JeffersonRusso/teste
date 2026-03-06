package br.com.orquestrator.orquestrator.domain.model;

import java.util.Map;
import java.util.List;
import java.util.Optional;

public sealed interface DataValue 
    permits DataValue.Text, DataValue.Numeric, DataValue.Logic, 
            DataValue.Mapping, DataValue.Sequence, DataValue.DomainObject, 
            DataValue.Empty {
    
    class FormatterHolder {
        private static SemanticFormatter formatter = (type, val) -> val.toString();
        public static void setFormatter(SemanticFormatter f) { formatter = f; }
        public static String format(String type, Object val) { return formatter.format(type, val); }
    }

    String semanticType();
    Object raw();

    record Text(String value, String semanticType) implements DataValue {
        @Override public Object raw() { return value; }
        @Override public String toString() { return FormatterHolder.format(semanticType, value); }
    }
    
    record Numeric(Number value, String semanticType) implements DataValue, Comparable<Number> {
        @Override public Object raw() { return value; }
        public double doubleValue() { return value.doubleValue(); }
        @Override public String toString() { return FormatterHolder.format(semanticType, value); }
        @Override public int compareTo(Number other) { return Double.compare(this.doubleValue(), other.doubleValue()); }
    }
    
    record Logic(Boolean value, String semanticType) implements DataValue {
        @Override public Object raw() { return value; }
        @Override public String toString() { return value.toString(); }
    }

    // Otimizado: Guarda a coleção original sem copiar
    record Mapping(Map<String, ?> fields, String semanticType) implements DataValue {
        @Override public Object raw() { return fields; }
    }

    record Sequence(List<?> items, String semanticType) implements DataValue {
        @Override public Object raw() { return items; }
    }

    record DomainObject(Object instance, String semanticType) implements DataValue {
        @Override public Object raw() { return instance; }
    }

    record Empty() implements DataValue {
        @Override public String semanticType() { return null; }
        @Override public Object raw() { return null; }
    }

    static DataValue of(Object val) { return of(val, null); }

    static DataValue of(Object val, String semanticType) {
        if (val == null) return new Empty();
        if (val instanceof DataValue dv) return dv;
        
        // Pattern Matching para criação direta (Zero Recursão)
        return switch (val) {
            case String s -> new Text(s, semanticType);
            case Number n -> new Numeric(n, semanticType);
            case Boolean b -> new Logic(b, semanticType);
            case Map m -> new Mapping((Map<String, ?>) m, semanticType);
            case List l -> new Sequence(l, semanticType);
            default -> new DomainObject(val, semanticType);
        };
    }

    default <T> Optional<T> as(Class<T> type) {
        Object r = raw();
        return (r != null && type.isInstance(r)) ? Optional.of(type.cast(r)) : Optional.empty();
    }
}
