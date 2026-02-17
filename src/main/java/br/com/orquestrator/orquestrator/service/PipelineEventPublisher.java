package br.com.orquestrator.orquestrator.service;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.event.PipelineExecutionSummary;
import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Componente especializado em traduzir o estado final do contexto em eventos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishFinished(ExecutionContext context, boolean success) {
        try {
            var trace = context.getTrace();
            
            var summary = new PipelineExecutionSummary(
                    context.getCorrelationId(),
                    context.getOperationType(),
                    trace.getStartTime(),
                    trace.getDurationMs(),
                    trace.getMetrics(),
                    context.getRoot(),
                    success
            );

            eventPublisher.publishEvent(new PipelineFinishedEvent(summary));
            log.debug("Evento PipelineFinishedEvent publicado para correlationId: {}", context.getCorrelationId());
        } catch (Exception e) {
            log.error("Falha ao publicar evento de finalização", e);
        }
    }
}
