package br.com.orquestrator.orquestrator.domain.model.definition;

import java.util.Map;
import java.util.Collections;

/**
 * FeatureDefinition: Representa a configuração de um interceptor dinâmico no domínio.
 * Totalmente desacoplado de bibliotecas de terceiros (Jackson/JsonNode).
 */
public record FeatureDefinition(
    String type,
    Map<String, Object> config
) {
    public FeatureDefinition {
        if (config == null) config = Collections.emptyMap();
    }
}
