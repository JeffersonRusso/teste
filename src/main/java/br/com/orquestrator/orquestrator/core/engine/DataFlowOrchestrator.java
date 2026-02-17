package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * DataFlowOrchestrator: O Maestro.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskRunner taskRunner;
    private final AsyncTaskExecutor asyncExecutor;
    private final PipelineEventPublisher eventPublisher;

    @Override
    public void run(ExecutionContext context, Pipeline pipeline) {
        ScopedValue.where(ContextHolder.CORRELATION_ID, context.getCorrelationId()).run(() -> {
            boolean success = false;
            try {
                for (var layer : pipeline.layers()) {
                    var tasks = layer.stream()
                            .<Runnable>map(def -> () -> taskRunner.run(def, context))
                            .toList();
                    
                    asyncExecutor.executeParallel(tasks, Duration.ofSeconds(30));
                }
                success = true;
            } catch (Exception e) {
                throw translate(e);
            } finally {
                finalizeExecution(context, success);
            }
        });
    }

    private void finalizeExecution(ExecutionContext context, boolean success) {
        context.getTrace().finish();
        eventPublisher.publishFinished(context, success);
    }

    private RuntimeException translate(Exception e) {
        return switch (e) {
            case TimeoutException _ -> new PipelineException("Pipeline Timeout");
            case InterruptedException _ -> {
                Thread.currentThread().interrupt();
                yield new PipelineException("Execution Interrupted");
            }
            case RuntimeException re -> re;
            default -> new PipelineException(e.getMessage(), e);
        };
    }
}
