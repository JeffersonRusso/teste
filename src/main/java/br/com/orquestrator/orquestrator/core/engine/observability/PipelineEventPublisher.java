package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    private final List<PipelineEventListener> listeners;

    public void publishPipelineStart(RequestIdentity identity, Map<String, Object> input) {
        listeners.forEach(l -> l.onPipelineStart(identity, input));
    }

    public void publishPipelineFinished(RequestIdentity identity, Map<String, Object> output, boolean success) {
        listeners.forEach(l -> l.onPipelineFinished(identity, output, success));
    }

    public void publishTaskStart(String nodeId) {
        listeners.forEach(l -> l.onTaskStart(nodeId));
    }

    public void publishTaskFinished(String nodeId, JsonNode result, long durationMs) {
        listeners.forEach(l -> l.onTaskFinished(nodeId, result, durationMs));
    }
}
