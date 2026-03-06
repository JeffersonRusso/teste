package br.com.orquestrator.orquestrator.domain.model;

import java.util.Optional;

/**
 * SemanticHandler: Define o comportamento de um tipo conceitual.
 * Permite que tipos "fakes" tenham comportamentos reais (soma, concat, máscara).
 */
public interface SemanticHandler {
    /** O nome do tipo que este handler atende (ex: CPF, VALOR_DESTINO). */
    String getTypeName();

    /** Como transformar o valor em String (Máscaras). */
    default String format(Object value) {
        return value != null ? value.toString() : "";
    }

    /** Operação de soma customizada. */
    default Object plus(Object value, Object other) {
        throw new UnsupportedOperationException("Operação '+' não suportada para o tipo " + getTypeName());
    }

    /** Operação de concatenação customizada. */
    default Object concat(Object value, Object other) {
        return value.toString() + other.toString();
    }
}
