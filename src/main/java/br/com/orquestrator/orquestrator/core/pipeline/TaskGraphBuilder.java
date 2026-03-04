package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskGraphBuilder: Transforma uma lista de tarefas em um Grafo Acíclico Dirigido (DAG).
 * Detecta ciclos e resolve dependências baseadas em dados.
 */
@Component
public class TaskGraphBuilder {

    public DirectedAcyclicGraph<TaskDefinition, DefaultEdge> build(List<TaskDefinition> tasks) {
        var graph = new DirectedAcyclicGraph<TaskDefinition, DefaultEdge>(DefaultEdge.class);
        tasks.forEach(graph::addVertex);

        Map<String, TaskDefinition> producers = mapProducers(tasks);

        for (var task : tasks) {
            connectDependencies(graph, task, producers);
        }

        return graph;
    }

    private Map<String, TaskDefinition> mapProducers(List<TaskDefinition> tasks) {
        Map<String, TaskDefinition> producers = new HashMap<>();
        for (var task : tasks) {
            if (task.outputs() != null) {
                task.outputs().values().forEach(outputKey -> producers.put(outputKey, task));
            }
        }
        return producers;
    }

    private void connectDependencies(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, 
                                     TaskDefinition consumer, 
                                     Map<String, TaskDefinition> producers) {
        if (consumer.inputs() == null) return;

        for (String inputKey : consumer.inputs().values()) {
            TaskDefinition producer = producers.get(inputKey);
            
            if (producer != null && !producer.equals(consumer)) {
                try {
                    graph.addEdge(producer, consumer);
                } catch (IllegalArgumentException e) {
                    throw new PipelineException(String.format(
                        "Ciclo detectado! A task [%s] cria uma dependência circular via dado '%s'", 
                        consumer.nodeId().value(), inputKey), e);
                }
            }
        }
    }
}
