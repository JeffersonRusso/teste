package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final List<PipelineEventListener> listeners;

    public void publishPipelineStarted(ContextMetadata metadata) {
        // Implementar no listener se necessário
    }

    public void publishPipelineFinished(ContextMetadata metadata, boolean success) {
        listeners.forEach(l -> l.onPipelineFinished(metadata, success));
    }

    public void publishTaskFinished(String nodeId, DataValue result, long durationMs) {
        listeners.forEach(l -> l.onTaskFinished(nodeId, result, durationMs));
    }
}
