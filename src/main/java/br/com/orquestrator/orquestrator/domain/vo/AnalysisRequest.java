package br.com.orquestrator.orquestrator.domain.vo;

import java.util.Map;
import java.util.Set;

/**
 * Análise Request: Encapsula os parâmetros de entrada.
 * Expurgado JsonNode em favor de Map.
 */
public record AnalysisRequest(
    String operationType,
    Map<String, String> headers,
    Map<String, Object> body,
    Set<String> requiredOutputs
) {
    public static AnalysisRequest simple(String type, Map<String, String> headers, Map<String, Object> body) {
        return new AnalysisRequest(type, headers, body, null);
    }
}
