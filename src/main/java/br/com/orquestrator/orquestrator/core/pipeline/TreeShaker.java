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
 * TreeShaker: Remove tarefas que não contribuem para os resultados finais solicitados.
 * Otimiza o pipeline para execução mínima necessária.
 */
@Component
public class TreeShaker {

    public Set<TaskDefinition> optimize(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, Set<String> requiredOutputs) {
        var targets = identifyTargetTasks(graph, requiredOutputs);
        
        // Se não houver outputs requeridos específicos, assume que todas as tasks são necessárias
        if (targets.isEmpty()) {
            return graph.vertexSet();
        }

        Set<TaskDefinition> necessaryTasks = new HashSet<>(targets);
        for (TaskDefinition target : targets) {
            necessaryTasks.addAll(graph.getAncestors(target));
        }
        
        return necessaryTasks;
    }

    private Set<TaskDefinition> identifyTargetTasks(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, Set<String> requiredOutputs) {
        if (requiredOutputs == null || requiredOutputs.isEmpty()) {
            return Collections.emptySet();
        }

        return graph.vertexSet().stream()
                .filter(task -> hasRequiredOutput(task, requiredOutputs))
                .collect(Collectors.toSet());
    }

    private boolean hasRequiredOutput(TaskDefinition task, Set<String> requiredOutputs) {
        if (task.outputs() == null) return false;
        return !Collections.disjoint(task.outputs().values(), requiredOutputs);
    }
}
