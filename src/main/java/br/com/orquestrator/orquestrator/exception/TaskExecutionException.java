package br.com.orquestrator.orquestrator.exception;

import lombok.Getter;

@Getter
public class TaskExecutionException extends PipelineException {
    
    private final String nodeId;

    public TaskExecutionException(String message) {
        super(message);
        this.nodeId = "unknown";
    }

    public TaskExecutionException(String message, String nodeId) {
        super(message);
        this.nodeId = nodeId;
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.nodeId = "unknown";
    }
}
