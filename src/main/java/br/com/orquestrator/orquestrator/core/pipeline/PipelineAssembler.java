package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.time.Duration;
import java.util.*;

@Service
public class PipelineAssembler {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");
    
    private final OrchestratorMetadataStore metadataStore;
    private final TaskSelector taskSelector;
    private final PipelineTimeoutCalculator timeoutCalculator;
    private final PipelineProvider pipelineProvider;
    private final TaskRegistry taskRegistry;

    public PipelineAssembler(OrchestratorMetadataStore metadataStore, TaskSelector taskSelector, 
                             PipelineTimeoutCalculator timeoutCalculator, PipelineProvider pipelineProvider, 
                             TaskRegistry taskRegistry) {
        this.metadataStore = metadataStore;
        this.taskSelector = taskSelector;
        this.timeoutCalculator = timeoutCalculator;
        this.pipelineProvider = pipelineProvider;
        this.taskRegistry = taskRegistry;
    }

    public Pipeline assemble(ExecutionContext context, FlowDefinition flowDef) {
        List<TaskDefinition> selected = taskSelector.select(metadataStore.getAllTasks(), context, flowDef);
        List<ContextTaskInitializer> initializers = pipelineProvider.getResolvedInitializers(context.getOperationType());
        Set<String> requiredOutputs = (flowDef != null && flowDef.requiredOutputs() != null) ? flowDef.requiredOutputs() : DEFAULT_TARGETS;
        Duration timeout = timeoutCalculator.calculate(selected);

        // Mapeia qual NodeId produz cada dado
        Map<String, NodeId> dataProviders = new HashMap<>();
        selected.forEach(def -> 
            def.getProduces().forEach(p -> dataProviders.put(p.name(), def.getNodeId()))
        );

        List<Pipeline.TaskNode> tasks = selected.stream()
                .map(def -> {
                    var deps = def.getRequires().stream()
                            .map(req -> dataProviders.get(req.name()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toUnmodifiableSet());
                    return new Pipeline.TaskNode(taskRegistry.getTask(def), def, deps);
                })
                .toList();

        return new Pipeline(tasks, timeout, requiredOutputs, initializers);
    }
}
