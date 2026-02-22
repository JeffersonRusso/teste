package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Pipeline: O Plano de Voo imutável e pré-otimizado.
 */
public record Pipeline(
    List<Task> executableTasks,
    List<TaskDefinition> taskDefinitions,
    Duration timeout,
    Set<String> requiredOutputs,
    List<ContextTaskInitializer> initializers,
    int[][] adjacencyMatrix,
    int[] dependencyCounts
) {}
