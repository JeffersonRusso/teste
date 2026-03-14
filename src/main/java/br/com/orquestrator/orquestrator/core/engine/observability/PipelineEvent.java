package br.com.orquestrator.orquestrator.core.engine.observability;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import java.util.Map;

/**
 * PipelineEvent: Contrato agnóstico para eventos de telemetria.
 */
public sealed interface PipelineEvent {
    
    record PipelineStarted(RequestIdentity identity, Map<String, Object> input) implements PipelineEvent {
        public String operationType() { return identity.getOperationType(); }
        public String correlationId() { return identity.getCorrelationId(); }
    }
    
    record PipelineFinished(RequestIdentity identity, Map<String, Object> output, boolean success) implements PipelineEvent {
        public String operationType() { return identity.getOperationType(); }
        public String correlationId() { return identity.getCorrelationId(); }
    }
    
    record TaskStarted(String nodeId) implements PipelineEvent {}
    
    record TaskFinished(String nodeId, Object result, long durationMs) implements PipelineEvent {}
    
    record TaskFailed(String nodeId, Throwable cause, long durationMs) implements PipelineEvent {}
}
