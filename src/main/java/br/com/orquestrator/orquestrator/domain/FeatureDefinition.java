package br.com.orquestrator.orquestrator.domain;

import java.util.Map;

/**
 * Definição de uma feature (interceptor) associada a uma task.
 * Java 21: Record para imutabilidade e clareza.
 */
public record FeatureDefinition(
    String type,
    String templateRef,
    Map<String, Object> config // Expurgado JsonNode
) {}
