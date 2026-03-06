package br.com.orquestrator.orquestrator.exception;

public class PipelineValidationException extends PipelineException {
    public PipelineValidationException(String message) {
        super(message);
    }

    public PipelineValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
