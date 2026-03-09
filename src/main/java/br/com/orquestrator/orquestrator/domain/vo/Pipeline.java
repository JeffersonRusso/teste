package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Pipeline: O plano de execução final e otimizado.
 * Representa um Grafo de Nós (DAG) pronto para ser processado pelo motor de sinais.
 */
public record Pipeline(
    Map<String, ExecutionNode> nodes,
    Duration timeout,
    Set<String> requiredOutputs // Agora usa Strings puras
) {
    public Collection<ExecutionNode> getNodes() { return nodes.values(); }
}
