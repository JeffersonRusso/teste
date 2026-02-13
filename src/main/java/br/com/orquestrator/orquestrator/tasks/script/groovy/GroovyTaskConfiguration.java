package br.com.orquestrator.orquestrator.tasks.script.groovy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GroovyTaskConfiguration(
    String scriptBody,
    String scriptName
) {}
