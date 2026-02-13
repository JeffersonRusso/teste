package br.com.orquestrator.orquestrator.exception;

public class TaskConfigurationException extends PipelineException {
    public TaskConfigurationException(String message) {
        super(message);
    }
    
    public TaskConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
