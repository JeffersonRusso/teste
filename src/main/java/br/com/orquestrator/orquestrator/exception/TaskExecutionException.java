package br.com.orquestrator.orquestrator.exception;

public class TaskExecutionException extends PipelineException {
    public TaskExecutionException(String message) {
        super(message);
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
