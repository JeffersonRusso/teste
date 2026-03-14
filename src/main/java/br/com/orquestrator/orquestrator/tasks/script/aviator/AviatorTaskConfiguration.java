package br.com.orquestrator.orquestrator.tasks.script.aviator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AviatorTaskConfiguration(
    String script
) {}
