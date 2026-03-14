package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.engine.flow.ExecutionNode;
import br.com.orquestrator.orquestrator.domain.model.vo.NodeId;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Pipeline: Representa o grafo de execução compilado e suas propriedades de execução.
 */
public record Pipeline(
    Map<NodeId, ExecutionNode> nodes,
    Duration timeout,
    Set<String> requiredOutputs,
    String executionStrategy // Estratégia de execução preferencial
) {
    public Collection<ExecutionNode> getNodes() { return nodes.values(); }
}
