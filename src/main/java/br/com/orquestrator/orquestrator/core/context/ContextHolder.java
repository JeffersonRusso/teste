package br.com.orquestrator.orquestrator.core.context;

import java.util.Optional;

/**
 * Gerencia o contexto de execução utilizando ScopedValue (Java 21) e ThreadLocal (Servlet).
 */
public final class ContextHolder {

    public static final ScopedValue<String> CORRELATION_ID = ScopedValue.newInstance();
    public static final ScopedValue<String> CURRENT_NODE = ScopedValue.newInstance();

    // ThreadLocal apenas para a thread do Servlet, antes de entrar no pipeline
    private static final ThreadLocal<String> TEMP_CORRELATION_ID = new ThreadLocal<>();

    private ContextHolder() {}

    public static void setTempCorrelationId(String id) {
        TEMP_CORRELATION_ID.set(id);
    }

    public static void clearTempCorrelationId() {
        TEMP_CORRELATION_ID.remove();
    }

    public static Optional<String> getCorrelationId() {
        if (CORRELATION_ID.isBound()) {
            return Optional.of(CORRELATION_ID.get());
        }
        return Optional.ofNullable(TEMP_CORRELATION_ID.get());
    }

    public static Optional<String> getCurrentNode() {
        return CURRENT_NODE.isBound() ? Optional.of(CURRENT_NODE.get()) : Optional.empty();
    }
}
