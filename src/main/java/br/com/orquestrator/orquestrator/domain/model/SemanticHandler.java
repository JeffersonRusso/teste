package br.com.orquestrator.orquestrator.domain.model;

/**
 * SemanticHandler: Interface de domínio para manipular dados baseados em tipagem semântica.
 */
public interface SemanticHandler {
    
    /**
     * Formata um valor para exibição/exposição (ex: log).
     */
    Object format(Object value);

    /**
     * Valida um valor baseado nas regras semânticas do domínio.
     */
    boolean isValid(Object value);

    /**
     * Retorna o nome do tipo semântico associado a este handler.
     */
    String getTypeName();
}
