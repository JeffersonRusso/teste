package br.com.orquestrator.orquestrator.domain.model;

/**
 * TaskOutput: Define o destino e a semântica de uma saída de tarefa.
 */
public record TaskOutput(
    String localKey,
    String targetSignal,
    String producedSemanticType // Sincronizado com a governança
) {}
