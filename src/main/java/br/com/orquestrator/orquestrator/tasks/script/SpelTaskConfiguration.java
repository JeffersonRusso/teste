package br.com.orquestrator.orquestrator.tasks.script;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuração imutável e tipada para a SpelTask.
 */
public record SpelTaskConfiguration(
    @JsonProperty("expression") String expression,
    @JsonProperty("required") boolean required
) {}
