package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventListener;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LogPipelineEventListener implements PipelineEventListener {

    @Override
    public void onPipelineFinished(ContextMetadata metadata, boolean success) {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        log.info("[{}] Pipeline finalizado | Operação: {} | ID: {} | Sucesso: {}", 
                timestamp, metadata.getOperationType(), metadata.getCorrelationId(), success);
    }

    @Override
    public void onTaskFinished(String nodeId, DataValue result, long durationMs) {
        if (log.isDebugEnabled()) {
            log.debug("Task [{}] finalizada em {}ms. Tipo de Retorno: {}", 
                nodeId, durationMs, result.getClass().getSimpleName());
        }
    }
}
