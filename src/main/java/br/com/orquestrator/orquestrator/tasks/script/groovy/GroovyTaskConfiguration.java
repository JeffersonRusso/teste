package br.com.orquestrator.orquestrator.tasks.script.groovy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * GroovyTaskConfiguration: Configuração pura e imutável.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroovyTaskConfiguration(
    String scriptName,
    String scriptBody,
    Map<String, Object> params
) {}
