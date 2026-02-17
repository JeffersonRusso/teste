package br.com.orquestrator.orquestrator.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Set;

/**
 * Análise Request: Encapsula os parâmetros de entrada.
 */
public record AnalysisRequest(
    String operationType,
    Map<String, String> headers,
    JsonNode body,
    Set<String> requiredOutputs
) {
    public static AnalysisRequest simple(String type, Map<String, String> headers, JsonNode body) {
        return new AnalysisRequest(type, headers, body, null);
    }
}
