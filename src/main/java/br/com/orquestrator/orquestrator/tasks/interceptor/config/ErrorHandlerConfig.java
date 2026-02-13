package br.com.orquestrator.orquestrator.tasks.interceptor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorHandlerConfig(
    String action,
    List<String> ignoreExceptions,
    List<String> ignoreNodes,
    JsonNode fallbackValue
) {
    public ErrorHandlerConfig {
        if (action == null) action = "FAIL";
        if (ignoreExceptions == null) ignoreExceptions = Collections.emptyList();
        if (ignoreNodes == null) ignoreNodes = Collections.emptyList();
    }
}
