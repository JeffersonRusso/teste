package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskExecutor taskExecutor;
    private final PipelineEventPublisher eventPublisher;
    private final DataBusFactory dataBusFactory;

    @Override
    public void run(final ExecutionContext context, final Pipeline pipeline) {
        DataBus dataBus = dataBusFactory.create(context, pipeline.getTasks());
        String correlationId = context.getCorrelationId();

        ScopedValue.where(ContextHolder.CORRELATION_ID, correlationId)
                .run(() -> executePipeline(context, pipeline, dataBus));
    }

    private void executePipeline(ExecutionContext context, Pipeline pipeline, DataBus dataBus) {
        boolean success = false;
        try {
            executeTasksInParallel(context, pipeline, dataBus);
            context.getTracker().finish();
            success = true;
        } catch (Exception e) {
            throw (RuntimeException) e;
        } finally {
            eventPublisher.publishFinished(context, success);
        }
    }

    private void executeTasksInParallel(ExecutionContext context, Pipeline pipeline, DataBus dataBus) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            pipeline.getTasks().forEach(taskDef ->
                    scope.fork(() -> {
                        taskExecutor.execute(taskDef, context, dataBus);
                        return null;
                    })
            );

            scope.joinUntil(context.runtime().deadline());
            scope.throwIfFailed();
        } catch (Exception e) {
            throw handleParallelException(e);
        }
    }

    private RuntimeException handleParallelException(Exception e) {
        return switch (e) {
            case TimeoutException _ -> new PipelineException("Pipeline excedeu o tempo limite");
            case InterruptedException _ -> {
                Thread.currentThread().interrupt();
                yield new PipelineException("Orquestração interrompida");
            }
            case java.util.concurrent.ExecutionException ee ->
                    (ee.getCause() instanceof RuntimeException re) ? re : new PipelineException(ee.getCause().getMessage(), ee.getCause());
            default -> (e instanceof RuntimeException re) ? re : new PipelineException(e.getMessage(), e);
        };
    }
}
