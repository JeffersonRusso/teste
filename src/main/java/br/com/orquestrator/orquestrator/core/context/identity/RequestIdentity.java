package br.com.orquestrator.orquestrator.core.context.identity;

public record RequestIdentity(
    String correlationId,
    String operationType,
    String orderId, // NOVO
    String executionId
) {}
