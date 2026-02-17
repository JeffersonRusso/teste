package br.com.orquestrator.orquestrator.tasks.script.groovy;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Configuração imutável e tipada para a GroovyTask.
 */
public record GroovyTaskConfiguration(
    @JsonProperty("scriptName") String scriptName,
    @JsonProperty("scriptBody") String scriptBody,
    @JsonProperty("params") Map<String, Object> additionalParams
) {}
