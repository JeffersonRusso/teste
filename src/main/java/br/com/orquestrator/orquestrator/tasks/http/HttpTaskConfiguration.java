package br.com.orquestrator.orquestrator.tasks.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Padrão: O body é sempre um Template (String).
 */
public record HttpTaskConfiguration(
    @JsonProperty("url") String urlTemplate,
    @JsonProperty("method") String method,
    @JsonProperty("body") String bodyTemplate,
    @JsonProperty("headers") Map<String, String> headersTemplates,
    @JsonProperty("timeout") Integer timeout
) {}
