package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.web.controller.dto.GraphResponse;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PipelineGraphService {

    private final PipelineFactory pipelineFactory;

    public GraphResponse generateGraph(final String operationType) {
        final ExecutionContext context = createDummyContext(operationType);
        // Passa null como versão para pegar a mais recente ativa (comportamento padrão)
        final Pipeline pipeline = pipelineFactory.create(context, null);

        final List<GraphResponse.Node> nodes = buildNodes(pipeline);
        final Map<String, String> producers = buildProducerMap(pipeline);
        final List<GraphResponse.Edge> edges = buildEdges(pipeline, producers);

        addContextNodeIfNeeded(nodes, edges);

        return new GraphResponse(nodes, edges);
    }

    private ExecutionContext createDummyContext(final String operationType) {
        Map<String, Object> initialData = new HashMap<>();
        initialData.put(ContextKey.OPERATION_TYPE, operationType);
        return new ExecutionContext("GRAPH_GENERATION", initialData, null);
    }

    private List<GraphResponse.Node> buildNodes(final Pipeline pipeline) {
        return pipeline.getTasks().stream()
                .map(task -> new GraphResponse.Node(
                        task.getNodeId().value(), 
                        task.getNodeId().value(), 
                        task.getType()))
                .collect(Collectors.toList());
    }

    private Map<String, String> buildProducerMap(final Pipeline pipeline) {
        Map<String, String> producers = new HashMap<>();
        for (TaskDefinition task : pipeline.getTasks()) {
            if (task.getProduces() != null) {
                task.getProduces().forEach(output -> producers.put(output.name(), task.getNodeId().value()));
            }
        }
        return producers;
    }

    private List<GraphResponse.Edge> buildEdges(final Pipeline pipeline, final Map<String, String> producers) {
        List<GraphResponse.Edge> edges = new ArrayList<>();
        for (TaskDefinition task : pipeline.getTasks()) {
            if (task.getRequires() != null) {
                for (DataSpec req : task.getRequires()) {
                    String sourceNode = producers.getOrDefault(req.name(), "CONTEXT");
                    edges.add(new GraphResponse.Edge(sourceNode, task.getNodeId().value(), req.name()));
                }
            }
        }
        return edges;
    }

    private void addContextNodeIfNeeded(final List<GraphResponse.Node> nodes, final List<GraphResponse.Edge> edges) {
        boolean hasContextSource = edges.stream().anyMatch(e -> "CONTEXT".equals(e.source()));
        if (hasContextSource) {
            nodes.add(0, new GraphResponse.Node("CONTEXT", "Input Context", "START"));
        }
    }
}
