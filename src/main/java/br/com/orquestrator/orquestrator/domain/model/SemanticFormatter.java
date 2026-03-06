package br.com.orquestrator.orquestrator.domain.model;

/**
 * SemanticFormatter: Define como um tipo semântico deve ser formatado para exibição (logs, UI).
 */
public interface SemanticFormatter {
    String format(String typeName, Object value);
}
