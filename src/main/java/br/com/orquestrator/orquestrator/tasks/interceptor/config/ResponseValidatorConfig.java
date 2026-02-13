package br.com.orquestrator.orquestrator.tasks.interceptor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseValidatorConfig(List<Rule> rules) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rule(
            String condition,
            String message,
            String errorCode,
            JsonNode metadata
    ) {}
}
