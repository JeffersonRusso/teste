package br.com.orquestrator.orquestrator.tasks.script;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScriptTaskConfiguration(
    String script
) {}
