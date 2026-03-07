package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.engine.binding.NormalizationStep;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pipeline: O plano de execução final e otimizado.
 */
public record Pipeline(
    Map<String, ExecutionNode> nodes,
    Duration timeout,
    Set<DataPath> requiredOutputs,
    List<NormalizationStep> normalizationPlan
) {
    public Collection<ExecutionNode> getNodes() { return nodes.values(); }
}
