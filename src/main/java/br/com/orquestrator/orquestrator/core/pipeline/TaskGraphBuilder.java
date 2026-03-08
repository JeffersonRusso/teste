package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskGraphBuilder: Transforma uma lista de tarefas em um Grafo Acíclico Dirigido (DAG).
 * Agora entende a Soberania de Sinais para ligar produtores e consumidores.
 */
@Component
public class TaskGraphBuilder {

    public DirectedAcyclicGraph<TaskDefinition, DefaultEdge> build(List<TaskDefinition> tasks) {
        var graph = new DirectedAcyclicGraph<TaskDefinition, DefaultEdge>(DefaultEdge.class);
        tasks.forEach(graph::addVertex);

        // Mapeia todos os sinais produzidos por cada task
        Map<String, TaskDefinition> signalProducers = new HashMap<>();
        for (var task : tasks) {
            if (task.outputs() != null) {
                task.outputs().values().forEach(out -> signalProducers.put(DataPath.of(out).getRoot(), task));
            }
        }

        for (var task : tasks) {
            connectDependencies(graph, task, signalProducers);
        }

        return graph;
    }

    private void connectDependencies(DirectedAcyclicGraph<TaskDefinition, DefaultEdge> graph, 
                                     TaskDefinition consumer, 
                                     Map<String, TaskDefinition> signalProducers) {
        if (consumer.inputs() == null) return;

        for (String inputPath : consumer.inputs().values()) {
            // Resolve o produtor pela raiz do sinal (ex: de 'perfil_cliente.id' busca 'perfil_cliente')
            String signalRoot = DataPath.of(inputPath).getRoot();
            TaskDefinition producer = signalProducers.get(signalRoot);
            
            if (producer != null && !producer.equals(consumer)) {
                try {
                    graph.addEdge(producer, consumer);
                } catch (IllegalArgumentException e) {
                    throw new PipelineException(String.format(
                        "Ciclo detectado! A task [%s] cria uma dependência circular via sinal '%s'", 
                        consumer.nodeId().value(), signalRoot), e);
                }
            }
        }
    }
}
