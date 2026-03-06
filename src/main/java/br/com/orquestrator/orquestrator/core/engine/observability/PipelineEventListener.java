package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.DataValue;

public interface PipelineEventListener {
    void onPipelineFinished(ContextMetadata metadata, boolean success);
    void onTaskFinished(String nodeId, DataValue result, long durationMs);
}
