package br.com.orquestrator.orquestrator.domain.vo;

import java.util.Set;

/**
 * Representa o contrato de dados pré-calculado de uma tarefa.
 * Java 21: Utiliza Records para imutabilidade e performance.
 */
public record TaskContract(
    Set<String> allowedInputs,
    Set<String> allowedOutputs
) {
    public TaskContract {
        allowedInputs = Set.copyOf(allowedInputs);
        allowedOutputs = Set.copyOf(allowedOutputs);
    }
}
