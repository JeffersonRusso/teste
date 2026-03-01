package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TaskGraphBuilder: Constrói e valida o grafo de dependências.
 */
@Component
public class TaskGraphBuilder {

    public DirectedAcyclicGraph<TaskDefinition, DefaultEdge> build(List<TaskDefinition> tasks) {
        var graph = new DirectedAcyclicGraph<TaskDefinition, DefaultEdge>(DefaultEdge.class);
        tasks.forEach(graph::addVertex);

        // Mapeia Output -> Task (Produtor)
        var producers = tasks.stream()
                .flatMap(t -> t.outputs().values().stream().map(out -> Map.entry(out, t)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        // Cria arestas baseadas nos inputs
        for (var task : tasks) {
            for (String inputKey : task.inputs().values()) {
                TaskDefinition producer = producers.get(inputKey);
                
                if (producer != null && !producer.equals(task)) {
                    try {
                        graph.addEdge(producer, task);
                    } catch (IllegalArgumentException e) {
                        // JGraphT lança isso se detectar um ciclo
                        throw new PipelineException(STR."Ciclo detectado! Task [\{task.nodeId()}] cria uma dependência circular via dado '\{inputKey}'", e);
                    }
                }
            }
        }
        return graph;
    }
}
