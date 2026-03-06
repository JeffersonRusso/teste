package br.com.orquestrator.orquestrator.infra;

public interface IdGenerator {
    /**
     * Gera um ID único e seguro.
     */
    String generate();

    /**
     * Gera um ID otimizado para performance (ex: sem traços, time-sortable).
     */
    String generateFastId();
}
