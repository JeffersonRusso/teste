package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Pipeline: Definição imutável do fluxo de execução.
 * Otimizado para acesso direto por índice (O(1)).
 */
public record Pipeline(
    List<TaskNode> tasks,
    Duration timeout,
    Set<String> requiredOutputs,
    List<ContextTaskInitializer> initializers
) {
    public record TaskNode(Task executable, TaskDefinition definition, Set<NodeId> dependencies) {}
}
