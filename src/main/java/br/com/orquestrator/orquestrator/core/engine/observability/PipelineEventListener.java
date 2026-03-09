package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface PipelineEventListener {
    void onPipelineStart(RequestIdentity identity, Map<String, Object> input);
    void onPipelineFinished(RequestIdentity identity, Map<String, Object> output, boolean success);
    void onTaskStart(String nodeId);
    void onTaskFinished(String nodeId, JsonNode result, long durationMs);
}
