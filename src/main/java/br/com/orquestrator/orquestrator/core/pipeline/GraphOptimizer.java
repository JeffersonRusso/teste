package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * GraphOptimizer: Identifica oportunidades de fusão de tarefas (Task Fusion).
 */
@Slf4j
@Component
public class GraphOptimizer {

    public Map<String, List<TaskDefinition>> identifyFusionGroups(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph) {
        Map<String, List<TaskDefinition>> groups = new HashMap<>();
        Set<TaskDefinition> fused = new HashSet<>();

        for (TaskDefinition node : graph.vertexSet()) {
            if (fused.contains(node)) continue;

            // Se o nó é CPU-bound e tem exatamente UM predecessor
            Set<DefaultEdge> incoming = graph.incomingEdgesOf(node);
            if (node.isCpuBound() && incoming.size() == 1) {
                TaskDefinition predecessor = graph.getEdgeSource(incoming.iterator().next());
                
                // E se o predecessor tem apenas este nó como sucessor
                if (graph.outDegreeOf(predecessor) == 1) {
                    log.debug("Otimização: Fundindo [{}] com [{}]", node.nodeId().value(), predecessor.nodeId().value());
                    
                    List<TaskDefinition> group = groups.computeIfAbsent(predecessor.nodeId().value(), k -> {
                        List<TaskDefinition> list = new ArrayList<>();
                        list.add(predecessor);
                        return list;
                    });
                    group.add(node);
                    fused.add(node);
                    fused.add(predecessor);
                }
            }
        }
        return groups;
    }
}
