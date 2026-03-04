package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * PipelineEventPublisher: Publica eventos seguindo normas de tempo ISO 8601.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    /**
     * Publica o evento de finalização.
     * Utiliza Instant para garantir conformidade com ISO 8601.
     */
    public void publishFinished(ContextMetadata metadata, boolean success) {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        
        log.info("[{}] Pipeline finalizado | Operação: {} | ID: {} | Sucesso: {}", 
                timestamp,
                metadata.getOperationType(), 
                metadata.getCorrelationId(), 
                success);
    }
}
