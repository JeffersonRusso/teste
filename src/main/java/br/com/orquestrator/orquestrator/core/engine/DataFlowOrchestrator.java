package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Service
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskRunner taskRunner;
    private final PipelineEventPublisher eventPublisher;
    
    // Cache para não reconstruir o grafo do DAG em cada request
    private final Map<Pipeline, DagPlan> planCache = new ConcurrentHashMap<>();

    public DataFlowOrchestrator(TaskRunner taskRunner, PipelineEventPublisher eventPublisher) {
        this.taskRunner = taskRunner;
        this.eventPublisher = eventPublisher;
    }

    private record DagPlan(int[] initialDeps, int[][] dependents, int[] initialNodes) {}

    private DagPlan getPlan(Pipeline pipeline) {
        return planCache.computeIfAbsent(pipeline, p -> {
            List<Pipeline.TaskNode> tasks = p.tasks();
            int size = tasks.size();
            int[] initialDeps = new int[size];
            List<Integer>[] adj = new List[size];
            List<Integer> starts = new ArrayList<>();

            Map<NodeId, Integer> idToIndex = new java.util.HashMap<>();
            for (int i = 0; i < size; i++) idToIndex.put(tasks.get(i).definition().getNodeId(), i);

            for (int i = 0; i < size; i++) {
                var node = tasks.get(i);
                initialDeps[i] = node.dependencies().size();
                if (initialDeps[i] == 0) starts.add(i);

                for (NodeId depId : node.dependencies()) {
                    Integer depIdx = idToIndex.get(depId);
                    if (depIdx != null) {
                        if (adj[depIdx] == null) adj[depIdx] = new ArrayList<>();
                        adj[depIdx].add(i);
                    }
                }
            }

            int[][] dependents = new int[size][];
            for (int i = 0; i < size; i++) {
                if (adj[i] != null) dependents[i] = adj[i].stream().mapToInt(Integer::intValue).toArray();
                else dependents[i] = new int[0];
            }

            return new DagPlan(initialDeps, dependents, starts.stream().mapToInt(Integer::intValue).toArray());
        });
    }

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        ScopedValue.where(ContextHolder.CORRELATION_ID, context.getCorrelationId())
            .run(() -> {
                var success = false;
                try {
                    runInitializers(context, pipeline);
                    executeDag(context, pipeline);
                    success = true;
                } catch (Exception e) {
                    throw translate(e);
                } finally {
                    finalizeExecution(context, success);
                }
            });
    }

    private void executeDag(ExecutionContext context, Pipeline pipeline) throws Exception {
        DagPlan plan = getPlan(pipeline);
        List<Pipeline.TaskNode> tasks = pipeline.tasks();
        
        // AtomicIntegerArray é LOCK-FREE. Não causa pinning de Virtual Threads.
        AtomicIntegerArray remaining = new AtomicIntegerArray(plan.initialDeps);

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Dispara apenas os nós iniciais
            for (int startIdx : plan.initialNodes) {
                forkTask(scope, startIdx, tasks, context, plan, remaining);
            }

            scope.joinUntil(Instant.now().plus(pipeline.timeout()));
            scope.throwIfFailed();
        }
    }

    private void forkTask(StructuredTaskScope.ShutdownOnFailure scope, 
                          int idx,
                          List<Pipeline.TaskNode> tasks,
                          ExecutionContext context,
                          DagPlan plan,
                          AtomicIntegerArray remaining) {
        
        scope.fork(() -> {
            Pipeline.TaskNode node = tasks.get(idx);
            taskRunner.run(node.executable(), node.definition(), context);
            
            int[] myDependents = plan.dependents[idx];
            for (int nextIdx : myDependents) {
                // DecrementAndGet é atômico e lock-free
                if (remaining.decrementAndGet(nextIdx) == 0) {
                    forkTask(scope, nextIdx, tasks, context, plan, remaining);
                }
            }
            return null;
        });
    }

    private void runInitializers(ExecutionContext context, Pipeline pipeline) {
        var inits = pipeline.initializers();
        if (inits != null) {
            for (int i = 0; i < inits.size(); i++) {
                try { inits.get(i).initialize(context); } catch (Exception _) {}
            }
        }
    }

    private void finalizeExecution(ExecutionContext context, boolean success) {
        context.getTrace().finish();
        eventPublisher.publishFinished(context, success);
    }

    private RuntimeException translate(Exception e) {
        if (e instanceof PipelineException pe) return pe;
        return new PipelineException(e.getMessage(), e);
    }
}
