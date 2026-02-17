package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Pipeline: O Plano de Voo imutável.
 */
public record Pipeline(
    List<TaskDefinition> tasks,
    Duration timeout,
    Set<String> requiredOutputs,
    List<List<TaskDefinition>> layers
) {}
