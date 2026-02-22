package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.DataMapping;
import java.util.List;
import java.util.Map;

/**
 * TaskRawData: Encapsula os dados brutos vindos do repositório.
 * Expurgado JsonNode em favor de Map.
 */
public record TaskRawData(
    String taskId,
    Integer version,
    String taskType,
    Map<String, Object> config,
    List<FeatureDefinition> features,
    String selectorExpression,
    Integer criticality,
    List<DataMapping> requires,
    List<DataMapping> produces,
    Map<String, Object> responseSchema
) {}
