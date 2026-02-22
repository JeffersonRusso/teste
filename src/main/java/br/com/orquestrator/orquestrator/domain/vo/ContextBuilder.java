package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.tracker.TraceContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builder fluído para ExecutionContext.
 * Otimizado para evitar bloqueios (Lock-free).
 */
public class ContextBuilder {
    private String correlationId;
    private String operationType;
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    public static ContextBuilder init(String operationType) {
        ContextBuilder builder = new ContextBuilder();
        builder.operationType = operationType;
        builder.data.put(ContextKey.OPERATION_TYPE, operationType);
        return builder;
    }

    public ContextBuilder withCorrelationId(String id) {
        this.correlationId = id;
        return this;
    }

    public ContextBuilder withData(String key, Object value) {
        if (value != null) this.data.put(key, value);
        return this;
    }

    public ExecutionContext build() {
        return new ExecutionContext(correlationId, operationType, new TraceContext(), data);
    }
}
