package br.com.orquestrator.orquestrator.exception;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PipelineException extends RuntimeException {

    private String nodeId;
    private final Map<String, Object> metadata = new HashMap<>();

    public PipelineException(String message) {
        super(message);
    }

    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Permite adicionar o ID do nó que originou o erro.
     */
    public PipelineException withNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    /**
     * Permite adicionar metadados contextuais (URL, status code, etc).
     */
    public PipelineException addMetadata(String key, Object value) {
        if (key != null && value != null) {
            this.metadata.put(key, value);
        }
        return this;
    }
}