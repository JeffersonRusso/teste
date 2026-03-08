package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PipelineEventPublisher: Publica eventos de telemetria para os listeners interessados.
 * Agora desacoplado do ContextHolder.
 */
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final List<PipelineEventListener> listeners;

    public void publishPipelineStarted(RequestIdentity identity) {
        listeners.forEach(l -> l.onPipelineStarted(identity));
    }

    public void publishPipelineFinished(RequestIdentity identity, boolean success) {
        listeners.forEach(l -> l.onPipelineFinished(identity, success));
    }

    public void publishTaskFinished(String nodeId, DataValue result, long durationMs) {
        listeners.forEach(l -> l.onTaskFinished(nodeId, result, durationMs));
    }
}
