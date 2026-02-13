package br.com.orquestrator.orquestrator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeatureDefinition(
        @JsonProperty("type") String type,
        @JsonProperty("templateRef") String templateRef,
        @JsonProperty("config") JsonNode config
) {}