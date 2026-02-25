package br.com.orquestrator.orquestrator.exception;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * PipelineException: Otimizada para alta performance.
 * Sobrescreve fillInStackTrace para evitar o custo de gerar stack traces em massa.
 */
@Getter
public class PipelineException extends RuntimeException {

    private String nodeId;
    private final Map<String, Object> metadata = new HashMap<>();

    public PipelineException(String message) {
        super(message, null, true, false);
    }

    public PipelineException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

    /**
     * Construtor protegido para permitir stacktrace se necessário.
     */
    protected PipelineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
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

    /**
     * Otimização crítica: Evita a criação da stack trace, que é extremamente cara.
     * Como o JMC mostrou alta alocação nesta classe, isso reduzirá a pressão no GC.
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
