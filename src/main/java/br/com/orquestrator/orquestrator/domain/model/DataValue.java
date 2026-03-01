package br.com.orquestrator.orquestrator.domain.model;

import java.util.Optional;

/**
 * Representa qualquer dado trafegado no orquestrador.
 * Elimina a dependência de Map<String, Object> na lógica de negócio.
 */
public sealed interface DataValue permits DataValue.Structured, DataValue.Scalar, DataValue.Empty {
    
    record Structured(Object record) implements DataValue {}
    record Scalar(Object value) implements DataValue {}
    record Empty() implements DataValue {}

    static DataValue of(Object val) {
        if (val == null) return new Empty();
        if (val instanceof DataValue dv) return dv;
        if (val.getClass().isRecord()) return new Structured(val);
        return new Scalar(val);
    }

    default <T> Optional<T> as(Class<T> type) {
        return switch (this) {
            case Structured(Object r) when type.isInstance(r) -> Optional.of(type.cast(r));
            case Scalar(Object v) when type.isInstance(v) -> Optional.of(type.cast(v));
            default -> Optional.empty();
        };
    }

    default Object raw() {
        return switch (this) {
            case Structured(Object r) -> r;
            case Scalar(Object v) -> v;
            case Empty() -> null;
        };
    }
}
