package br.com.orquestrator.orquestrator.tasks.script;

/**
 * Configuração imutável e tipada para a SpelTask.
 * Representa o contrato de avaliação de expressões SpEL.
 */
public record SpelTaskConfiguration(
    String expression,
    boolean required
) {}
