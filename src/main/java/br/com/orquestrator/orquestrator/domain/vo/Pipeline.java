package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Pipeline: O plano de execução final e otimizado.
 */
public record Pipeline(
    Map<String, TaskNode> nodes,
    Duration timeout,
    Set<String> requiredOutputs,
    List<DataMarshaller.NormalizationStep> normalizationPlan // PLANO PRÉ-COMPILADO
) {
    public Collection<TaskNode> getNodes() { return nodes.values(); }

    public record TaskNode(
        Task executable,
        String nodeId,
        String type,
        List<CompletableFuture<Void>> dependencies,
        List<CompletableFuture<Void>> signalsToEmit,
        boolean failFast,
        String guardCondition,
        Set<String> activationTags,
        long timeoutMs
    ) {}

    public record InputInstruction(String contextKey, boolean required) {}
    public record OutputInstruction(String targetKey) {}
}
