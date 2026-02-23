package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

/**
 * DataFlowOrchestrator: Motor DAG purista usando Java 21.
 * Orquestração baseada em sinais de dados (Data-Driven) com Virtual Threads.
 */
@Service
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskRunner taskRunner;
    private final PipelineEventPublisher eventPublisher;

    public DataFlowOrchestrator(TaskRunner taskRunner, PipelineEventPublisher eventPublisher) {
        this.taskRunner = taskRunner;
        this.eventPublisher = eventPublisher;
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
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Sinais de conclusão baseados na identidade do nó (NodeId)
            var taskSignals = new ConcurrentHashMap<NodeId, CompletableFuture<Void>>();
            pipeline.tasks().forEach(n -> taskSignals.put(n.definition().getNodeId(), new CompletableFuture<>()));

            for (var node : pipeline.tasks()) {
                scope.fork(() -> {
                    // 1. Aguarda as tarefas das quais eu dependo
                    for (var depId : node.dependencies()) {
                        taskSignals.get(depId).join();
                    }

                    // 2. Executa e sinaliza conclusão
                    taskRunner.run(node.executable(), node.definition(), context);
                    taskSignals.get(node.definition().getNodeId()).complete(null);
                    return null;
                });
            }

            scope.joinUntil(Instant.now().plus(pipeline.timeout()));
            scope.throwIfFailed();
        }
    }

    private void runInitializers(ExecutionContext context, Pipeline pipeline) {
        if (pipeline.initializers() != null) {
            pipeline.initializers().forEach(init -> {
                try { init.initialize(context); } catch (Exception _) {}
            });
        }
    }

    private void finalizeExecution(ExecutionContext context, boolean success) {
        context.getTrace().finish();
        eventPublisher.publishFinished(context, success);
    }

    private RuntimeException translate(Exception e) {
        return switch (e) {
            case TimeoutException _ -> new PipelineException("Pipeline Timeout: " + e.getMessage());
            case InterruptedException _ -> {
                Thread.currentThread().interrupt();
                yield new PipelineException("Pipeline Interrupted");
            }
            case ExecutionException ex when ex.getCause() instanceof RuntimeException re -> re;
            case ExecutionException ex -> new PipelineException(ex.getCause().getMessage(), ex.getCause());
            case RuntimeException re -> re;
            default -> new PipelineException(e.getMessage(), e);
        };
    }
}
