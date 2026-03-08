package br.com.orquestrator.orquestrator.core.context.identity;

import java.util.Set;

/**
 * RequestIdentity: Identidade única da requisição.
 * Agora inclui tags para suportar a resolução de pipelines dinâmicos.
 */
public record RequestIdentity(
    String correlationId,
    String operationType,
    String orderId,
    String executionId,
    Set<String> tags
) {
    public RequestIdentity {
        tags = tags != null ? Set.copyOf(tags) : Set.of();
    }

    public String getOperationType() { return operationType; }
    public String getCorrelationId() { return correlationId; }
    public Set<String> getTags() { return tags; }
}
