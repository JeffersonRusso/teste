package br.com.orquestrator.orquestrator.domain.model;

import java.util.List;
import java.util.Set;

/**
 * Definição de um fluxo de orquestração.
 */
public record FlowDefinition(
    String operationType,
    Integer version,
    Set<String> requiredOutputs,
    List<TaskReference> allowedTasks
) {
    public record TaskReference(String id, Integer version) {}
}
