package br.com.orquestrator.orquestrator.service;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.event.PipelineExecutionSummary;
import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

/**
 * Componente especializado em traduzir o estado final do contexto
 * em eventos de negócio para o ecossistema.
 * Desacopla o motor de orquestração da infraestrutura de eventos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final ConversionService conversionService;

    /**
     * Consolida as métricas e o estado final e publica o evento de finalização.
     */
    public void publishFinished(ExecutionContext context, boolean success) {
        try {
            var tracker = context.getTracker();
            
            // Garante que o tracker foi finalizado se alguém esqueceu
            if (tracker.getTotalDurationMs() <= 0) {
                tracker.finish();
            }

            var summary = new PipelineExecutionSummary(
                    context.getCorrelationId(),
                    conversionService.convert(context.get(ContextKey.OPERATION_TYPE), String.class),
                    tracker.getStartTime(),
                    tracker.getTotalDurationMs(),
                    tracker.getMetrics(),
                    context.getDataStore(),
                    success
            );

            eventPublisher.publishEvent(new PipelineFinishedEvent(summary));
            log.debug("Evento PipelineFinishedEvent publicado para correlationId: {}", context.getCorrelationId());
        } catch (Exception e) {
            log.error("Falha ao publicar evento de finalização", e);
        }
    }
}
