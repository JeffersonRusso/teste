package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.DataValue;

/**
 * PipelineEventListener: Contrato para escutar eventos de execução.
 */
public interface PipelineEventListener {
    default void onPipelineStarted(RequestIdentity identity) {}
    void onPipelineFinished(RequestIdentity identity, boolean success);
    void onTaskFinished(String nodeId, DataValue result, long durationMs);
}
