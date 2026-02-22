package br.com.orquestrator.orquestrator.domain.model;

import java.util.List;
import java.util.Set;

/**
 * Modelo de domínio puro para a configuração de um fluxo.
 */
public record FlowConfig(
    String operationType,
    Integer version,
    Set<String> requiredOutputs,
    List<TaskReference> allowedTasks
) {
    public record TaskReference(String id, Integer version) {}
}