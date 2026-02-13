package br.com.orquestrator.orquestrator.tasks.script.groovy;

import java.util.Map;

/**
 * Configuração imutável e tipada para a GroovyTask.
 * Representa o contrato de negócio da tarefa, isolado da infraestrutura de parsing.
 */
public record GroovyTaskConfiguration(
    String scriptName,
    String scriptBody,
    Map<String, Object> additionalParams
) {}
