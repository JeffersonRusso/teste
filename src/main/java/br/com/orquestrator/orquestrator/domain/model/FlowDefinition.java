package br.com.orquestrator.orquestrator.domain.model;

import java.util.Set;

public record FlowDefinition(
    String operationType,
    Set<String> requiredOutputs,
    Set<TaskReference> allowedTasks // Lista de referências (ID + Versão)
) {}
