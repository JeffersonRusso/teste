package br.com.orquestrator.orquestrator.domain.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record PipelineDefinition(
    String operationType,
    int version,
    long timeoutMs,
    Map<String, String> inputMapping, // Mapeamento de entrada (Raw -> Global)
    Set<String> defaultRequiredOutputs,
    List<TaskDefinition> tasks
) {}
