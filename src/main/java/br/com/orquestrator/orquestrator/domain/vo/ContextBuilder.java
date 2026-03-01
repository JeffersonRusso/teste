package br.com.orquestrator.orquestrator.domain.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder fluído para ExecutionContext.
 */
public class ContextBuilder {
    private String correlationId;
    private String operationType;
    private final Map<String, Object> data = new HashMap<>();

    public static ContextBuilder init(String operationType) {
        ContextBuilder builder = new ContextBuilder();
        builder.operationType = operationType;
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
        return new ExecutionContext(correlationId, operationType, data);
    }
}
