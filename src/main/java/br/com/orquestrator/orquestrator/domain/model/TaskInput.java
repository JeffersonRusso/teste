package br.com.orquestrator.orquestrator.domain.model;

/**
 * TaskInput: Define a origem e a semântica de uma entrada de tarefa.
 */
public record TaskInput(
    String localKey,
    String sourceSignal,
    String sourcePath,
    boolean required,
    String expectedSemanticType // Sincronizado com a governança
) {}
