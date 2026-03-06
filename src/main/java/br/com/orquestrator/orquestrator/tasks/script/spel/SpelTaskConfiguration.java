package br.com.orquestrator.orquestrator.tasks.script.spel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpelTaskConfiguration(
    String expression
) {}
