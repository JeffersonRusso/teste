package br.com.orquestrator.orquestrator.tasks.script.dmn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DmnTaskConfiguration(
    String dmnFile,
    String decisionKey
) {}
