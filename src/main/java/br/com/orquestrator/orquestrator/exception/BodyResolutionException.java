package br.com.orquestrator.orquestrator.exception;

/**
 * Exceção lançada quando a resolução de uma expressão SpEL
 * falha durante a construção do corpo HTTP.
 */
public class BodyResolutionException extends RuntimeException {
    public BodyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
