package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pipeline: O executável final.
 */
public record Pipeline(
    Map<String, TaskNode> tasks,
    Duration timeout,
    Set<String> requiredOutputs,
    Map<String, String> inputMapping
) {
    public record TaskNode(
        Task executable,
        String nodeId,
        String type,
        List<InputInstruction> inputs,
        List<OutputInstruction> outputs,
        boolean failFast,
        String guardCondition,
        Set<String> activationTags,
        long timeoutMs
    ) {}

    public record InputInstruction(String contextKey, boolean required) {}
    
    /**
     * OutputInstruction: Apenas a chave onde o resultado da task será gravado.
     */
    public record OutputInstruction(String targetKey) {}
    
    public Collection<TaskNode> getNodes() {
        return tasks.values();
    }
}
