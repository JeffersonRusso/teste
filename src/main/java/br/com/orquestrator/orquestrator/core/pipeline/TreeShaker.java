package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TreeShaker: Especialista em otimização de alcance.
 * Encapsula toda a inteligência de identificar o que é necessário para o objetivo.
 */
@Component
public class TreeShaker {

    /**
     * Filtra o grafo mantendo apenas os vértices necessários para produzir os outputs requeridos.
     */
    public Set<TaskDefinition> optimize(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, Set<String> requiredOutputs) {
        var targets = identifyTargets(graph, requiredOutputs);
        if (targets.isEmpty()) return graph.vertexSet();

        Set<TaskDefinition> necessary = new HashSet<>(targets);
        for (TaskDefinition target : targets) {
            necessary.addAll(graph.getAncestors(target));
        }
        return necessary;
    }

    private Set<TaskDefinition> identifyTargets(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, Set<String> requiredOutputs) {
        if (requiredOutputs == null || requiredOutputs.isEmpty()) return Set.of();
        return graph.vertexSet().stream()
                .filter(t -> !Collections.disjoint(t.outputs().values(), requiredOutputs))
                .collect(Collectors.toSet());
    }
}
