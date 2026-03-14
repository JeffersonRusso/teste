package br.com.orquestrator.orquestrator.infra.observability;

import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * LogPipelineEventListener: Adapter que agora utiliza o mecanismo nativo de eventos do Spring.
 */
@Slf4j
@Component
public class LogPipelineEventListener {

    @EventListener
    public void onPipelineEvent(PipelineEvent event) {
        switch (event) {
            case PipelineEvent.PipelineStarted e -> 
                log.info("[{}] Pipeline iniciado | Operação: {} | ID: {}", 
                        Instant.now(), e.operationType(), e.correlationId());

            case PipelineEvent.PipelineFinished e -> 
                log.info("[{}] Pipeline finalizado | Operação: {} | ID: {} | Sucesso: {}", 
                        Instant.now(), e.operationType(), e.correlationId(), e.success());

            case PipelineEvent.TaskStarted e -> 
                log.debug("Task [{}] iniciada.", e.nodeId());

            case PipelineEvent.TaskFinished e -> 
                log.debug("Task [{}] finalizada em {}ms | Resultado: {}", 
                        e.nodeId(), e.durationMs(), e.result());

            case PipelineEvent.TaskFailed e -> 
                log.error("Task [{}] FALHOU em {}ms | Causa: {}", 
                        e.nodeId(), e.durationMs(), e.cause().getMessage());
        }
    }
}
