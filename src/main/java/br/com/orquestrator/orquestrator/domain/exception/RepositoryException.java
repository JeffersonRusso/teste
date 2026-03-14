package br.com.orquestrator.orquestrator.domain.exception;

/**
 * RepositoryException: Exceção genérica para falhas na camada de persistência.
 * O Domínio não deve conhecer detalhes de JPA ou SQL.
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
