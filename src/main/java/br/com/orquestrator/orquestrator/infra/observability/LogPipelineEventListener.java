package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventListener;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class LogPipelineEventListener implements PipelineEventListener {

    @Override
    public void onPipelineStart(RequestIdentity identity, Map<String, Object> input) {
        log.info("[{}] Pipeline iniciado | Operação: {} | ID: {}", 
            Instant.now(), identity.getOperationType(), identity.getCorrelationId());
    }

    @Override
    public void onPipelineFinished(RequestIdentity identity, Map<String, Object> output, boolean success) {
        log.info("[{}] Pipeline finalizado | Operação: {} | ID: {} | Sucesso: {}", 
            Instant.now(), identity.getOperationType(), identity.getCorrelationId(), success);
    }

    @Override
    public void onTaskStart(String nodeId) {
        log.debug("Iniciando task: {}", nodeId);
    }

    @Override
    public void onTaskFinished(String nodeId, JsonNode result, long durationMs) {
        log.debug("Task [{}] finalizada em {}ms | Resultado: {}", nodeId, durationMs, result);
    }
}
