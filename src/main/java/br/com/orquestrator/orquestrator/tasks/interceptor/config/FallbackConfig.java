package br.com.orquestrator.orquestrator.tasks.interceptor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FallbackConfig(
    JsonNode value // O valor JSON que será retornado em caso de erro
) {}
