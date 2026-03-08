package br.com.orquestrator.orquestrator.exception;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * PipelineException: Otimizada para alta performance e rastreabilidade.
 * Carrega a identidade da execução para evitar dependência de ThreadLocal/MDC.
 */
@Getter
public class PipelineException extends RuntimeException {

    private String nodeId;
    private String correlationId;
    private String operation;
    private final Map<String, Object> metadata = new HashMap<>();

    public PipelineException(String message) {
        super(message, null, true, false);
    }

    public PipelineException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

    public PipelineException withIdentity(String correlationId, String operation) {
        this.correlationId = correlationId;
        this.operation = operation;
        return this;
    }

    public PipelineException withNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public PipelineException addMetadata(String key, Object value) {
        if (key != null && value != null) {
            this.metadata.put(key, value);
        }
        return this;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
