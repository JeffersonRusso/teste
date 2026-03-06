package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TreeShaker {

    public Set<TaskDefinition> optimize(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, Set<String> requiredOutputs) {
        var targets = identifyTargetTasks(graph, requiredOutputs);
        
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
                .filter(task -> providesAny(task, requiredOutputs))
                .collect(Collectors.toSet());
    }

    private boolean providesAny(TaskDefinition task, Set<String> requiredOutputs) {
        if (task.outputs() == null) return false;

        for (String produced : task.outputs().values()) {
            DataPath producedPath = DataPath.of(produced);
            for (String required : requiredOutputs) {
                if (producedPath.provides(DataPath.of(required))) return true;
            }
        }
        return false;
    }
}
