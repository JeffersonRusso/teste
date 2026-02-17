package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * TaskRawData: Encapsula os dados brutos vindos do repositório.
 */
public record TaskRawData(
    String taskId,
    Integer version,
    String taskType,
    JsonNode configJson,
    FeaturePhases taskFeatures,
    JsonNode profileFeaturesJson,
    String selectorExpression,
    Integer criticality,
    JsonNode requiresJson,
    JsonNode producesJson,
    JsonNode responseSchema
) {}
