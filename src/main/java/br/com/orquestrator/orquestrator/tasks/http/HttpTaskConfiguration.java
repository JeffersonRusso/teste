package br.com.orquestrator.orquestrator.tasks.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HttpTaskConfiguration(
    @JsonProperty("url") String urlTemplate,
    @JsonProperty("method") String method,
    @JsonProperty("body") JsonNode bodyConfig,
    @JsonProperty("headers") JsonNode headersConfig
) {}
