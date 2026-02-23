package br.com.orquestrator.orquestrator.domain.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Definição de um fluxo de orquestração.
 * Otimizado para lookup O(1) no hot path.
 */
public record FlowDefinition(
    String operationType,
    Integer version,
    Set<String> requiredOutputs,
    List<TaskReference> allowedTasks,
    Set<String> allowedTaskKeys // Cache de chaves para lookup rápido
) {
    public FlowDefinition(String operationType, Integer version, Set<String> requiredOutputs, List<TaskReference> allowedTasks) {
        this(operationType, version, requiredOutputs, allowedTasks, 
             allowedTasks != null ? allowedTasks.stream()
                 .map(ref -> ref.id() + ":" + (ref.version() != null ? ref.version() : 1))
                 .collect(Collectors.toUnmodifiableSet()) : null);
    }

    public record TaskReference(String id, Integer version) {}
}
