package br.com.orquestrator.orquestrator.infra.listener;

import br.com.orquestrator.orquestrator.config.AsyncConfig;
import br.com.orquestrator.orquestrator.domain.event.PipelineFinishedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener de auditoria assíncrono e isolado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final ObjectMapper objectMapper;

    /**
     * Processa o log de auditoria em um executor dedicado para não competir com o motor principal.
     */
    @Async(AsyncConfig.AUDIT_EXECUTOR)
    @EventListener
    public void onPipelineFinished(PipelineFinishedEvent event) {
        if (event == null || event.summary() == null) return;

        try {
            if (log.isInfoEnabled()) {
                String jsonSummary = objectMapper.writeValueAsString(event.summary());
                log.info("AUDIT_LOG|{}|{}", event.summary().correlationId(), jsonSummary);
            }
        } catch (Exception e) {
            log.error("Falha ao processar log de auditoria para [{}]: {}", 
                    event.summary().correlationId(), e.getMessage());
        }
    }
}
