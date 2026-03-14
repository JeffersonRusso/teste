package br.com.orquestrator.orquestrator.domain.model.definition;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * PipelineDefinition: O projeto arquitetural de um processo de negócio.
 */
public record PipelineDefinition(
        String operationType,
        int version,
        long timeoutMs,
        Set<String> defaultRequiredOutputs,
        List<TaskDefinition> tasks,
        String executionStrategy // Novo campo de estratégia
) {
    public PipelineDefinition {
        if (defaultRequiredOutputs == null) defaultRequiredOutputs = Collections.emptySet();
        if (tasks == null) tasks = Collections.emptyList();
        if (executionStrategy == null || executionStrategy.isBlank()) executionStrategy = "ASYNC";
    }
}