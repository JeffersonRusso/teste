package br.com.orquestrator.orquestrator.domain.exception;

/**
 * PipelineNotFoundException: Exceção específica para quando um pipeline não é encontrado.
 */
public class PipelineNotFoundException extends RepositoryException {

    public PipelineNotFoundException(String message) {
        super(message);
    }
}
