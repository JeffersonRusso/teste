package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.ScopedValue;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * DataFlowOrchestrator: Motor DAG Iterativo de Ultra-Performance.
 * Elimina a recursão para reduzir drasticamente o uso de StackChunk e Thread Churn.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskRunner taskRunner;
    private final PipelineEventPublisher eventPublisher;
    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        final String correlationId = context.getCorrelationId();

        ScopedValue.where(ContextHolder.CORRELATION_ID, correlationId).run(() -> {
            boolean success = false;
            try {
                runInitializers(context, pipeline);

                final List<Task> tasks = pipeline.executableTasks();
                final List<TaskDefinition> defs = pipeline.taskDefinitions();
                final int taskCount = tasks.size();
                final int[][] adj = pipeline.adjacencyMatrix();
                final int[] depCounts = pipeline.dependencyCounts();

                final AtomicIntegerArray counters = new AtomicIntegerArray(depCounts);
                final CountDownLatch latch = new CountDownLatch(taskCount);

                // Dispara tasks iniciais
                for (int i = 0; i < taskCount; i++) {
                    if (depCounts[i] == 0) {
                        enqueue(i, tasks, defs, adj, counters, latch, correlationId, context);
                    }
                }

                if (!latch.await(pipeline.timeout().toMillis(), TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Pipeline Timeout");
                }
                success = true;
            } catch (Exception e) {
                throw translate(e);
            } finally {
                finalizeExecution(context, success);
            }
        });
    }

    private void enqueue(int taskIdx, List<Task> tasks, List<TaskDefinition> defs, int[][] adj,
                         AtomicIntegerArray counters, CountDownLatch latch, String correlationId, ExecutionContext context) {
        virtualExecutor.execute(() -> {
            try {
                // Execução da Task via TaskRunner para garantir processamento de resultados
                taskRunner.run(tasks.get(taskIdx), defs.get(taskIdx), context);

                // Notificação de sucessores (Iterativa, não recursiva)
                final int[] nextTasks = adj[taskIdx];
                for (int nextIdx : nextTasks) {
                    if (counters.decrementAndGet(nextIdx) == 0) {
                        // Dispara o próximo nível em uma nova Virtual Thread para manter a stack rasa
                        enqueue(nextIdx, tasks, defs, adj, counters, latch, correlationId, context);
                    }
                }
            } finally {
                latch.countDown();
            }
        });
    }

    private void runInitializers(ExecutionContext context, Pipeline pipeline) {
        if (pipeline.initializers() == null) return;
        for (var initializer : pipeline.initializers()) {
            try { initializer.initialize(context); } catch (Exception e) {}
        }
    }

    private void finalizeExecution(ExecutionContext context, boolean success) {
        context.getTrace().finish();
        eventPublisher.publishFinished(context, success);
    }

    private RuntimeException translate(Exception e) {
        if (e instanceof TimeoutException) return new PipelineException("Pipeline Timeout");
        return (e instanceof RuntimeException re) ? re : new PipelineException(e.getMessage(), e);
    }
}
