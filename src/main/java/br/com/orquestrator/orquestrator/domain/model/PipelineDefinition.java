package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.vo.SignalBinding;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record PipelineDefinition(
    String operationType,
    int version,
    long timeoutMs,
    Map<String, SignalBinding> inputMapping,
    Set<String> defaultRequiredOutputs,
    List<TaskDefinition> tasks
) {}
