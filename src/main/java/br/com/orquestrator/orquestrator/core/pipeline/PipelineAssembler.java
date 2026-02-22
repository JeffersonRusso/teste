package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineAssembler {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");
    
    private final OrchestratorMetadataStore metadataStore;
    private final TaskSelector taskSelector;
    private final PipelineTimeoutCalculator timeoutCalculator;
    private final PipelineProvider pipelineProvider;
    private final TaskRegistry taskRegistry;

    public Pipeline assemble(ExecutionContext context, FlowDefinition flowDef) {
        List<TaskDefinition> selected = taskSelector.select(metadataStore.getAllTasks(), context, flowDef);
        List<ContextTaskInitializer> initializers = pipelineProvider.getResolvedInitializers(context.getOperationType());
        Set<String> requiredOutputs = (flowDef != null && flowDef.requiredOutputs() != null) ? flowDef.requiredOutputs() : DEFAULT_TARGETS;
        Duration timeout = timeoutCalculator.calculate(selected);

        int taskCount = selected.size();
        List<Task> executableTasks = new ArrayList<>(taskCount);
        Map<String, Integer> dataToProducerIndex = new HashMap<>(taskCount * 2);

        for (int i = 0; i < taskCount; i++) {
            TaskDefinition def = selected.get(i);
            executableTasks.add(taskRegistry.getTask(def));
            
            List<DataSpec> produces = def.getProduces();
            if (produces != null) {
                for (DataSpec p : produces) dataToProducerIndex.put(p.name(), i);
            }
        }

        int[][] adj = new int[taskCount][];
        int[] depCounts = new int[taskCount];
        List<Integer>[] tempAdj = new List[taskCount];

        for (int i = 0; i < taskCount; i++) {
            List<DataSpec> requires = selected.get(i).getRequires();
            if (requires != null) {
                for (DataSpec req : requires) {
                    Integer producerIdx = dataToProducerIndex.get(req.name());
                    if (producerIdx != null && producerIdx != i) {
                        depCounts[i]++;
                        if (tempAdj[producerIdx] == null) tempAdj[producerIdx] = new ArrayList<>();
                        tempAdj[producerIdx].add(i);
                    }
                }
            }
        }

        for (int i = 0; i < taskCount; i++) {
            adj[i] = (tempAdj[i] != null) ? tempAdj[i].stream().mapToInt(Integer::intValue).toArray() : new int[0];
        }

        return new Pipeline(executableTasks, selected, timeout, requiredOutputs, initializers, adj, depCounts);
    }
}
