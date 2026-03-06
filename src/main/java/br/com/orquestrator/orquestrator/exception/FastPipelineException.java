package br.com.orquestrator.orquestrator.exception;

/**
 * FastPipelineException: Exceção de alta performance que não preenche o stacktrace.
 * Ideal para erros de validação e controle de fluxo em sistemas de alto TPS.
 */
public class FastPipelineException extends PipelineException {
    
    public FastPipelineException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // O "Pulo do Gato": Não preencher o stacktrace economiza 5% de CPU
        return this;
    }
}
