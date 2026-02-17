package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.service.PipelineEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

/**
 * Orquestrador de Fluxo de Dados: O Maestro.
 * Executa o pipeline em camadas paralelas resolvidas previamente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataFlowOrchestrator implements PipelineEngine {

    private final TaskRunner taskRunner;
    private final PipelineEventPublisher eventPublisher;

    @Override
    public void run(final ExecutionContext context, final Pipeline pipeline) {
        ScopedValue.where(ContextHolder.CORRELATION_ID, context.getCorrelationId())
                .run(() -> {
                    boolean success = false;
                    try {
                        // Executa cada camada em sequência, e as tasks da camada em paralelo
                        for (var layer : pipeline.getLayers()) {
                            executeLayer(layer, context);
                        }
                        context.getTracker().finish();
                        success = true;
                    } catch (Exception e) {
                        throw handleException(e);
                    } finally {
                        eventPublisher.publishFinished(context, success);
                    }
                });
    }

    private void executeLayer(java.util.List<br.com.orquestrator.orquestrator.domain.model.TaskDefinition> layer, ExecutionContext context) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            layer.forEach(task -> scope.fork(() -> {
                taskRunner.run(task, context);
                return null;
            }));
            // Timeout de segurança por camada
            scope.joinUntil(Instant.now().plusSeconds(30));
            scope.throwIfFailed();
        }
    }

    private RuntimeException handleException(Exception e) {
        if (e instanceof TimeoutException) return new PipelineException("Pipeline Timeout");
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return new PipelineException("Interrupted");
        }
        return (e instanceof RuntimeException re) ? re : new PipelineException(e.getMessage(), e);
    }
}
