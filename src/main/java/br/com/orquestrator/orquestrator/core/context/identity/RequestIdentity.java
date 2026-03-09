package br.com.orquestrator.orquestrator.core.context.identity;

import io.micrometer.observation.Observation;
import lombok.Getter;

import java.util.Set;

@Getter
public class RequestIdentity extends Observation.Context {
    private final String correlationId;
    private final String operationType;
    private final String orderId;
    private final String executionId;
    private final Set<String> activeTags;

    public RequestIdentity(String correlationId, String operationType, String orderId, String executionId, Set<String> activeTags) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.orderId = orderId;
        this.executionId = executionId;
        this.activeTags = activeTags;
    }
}
