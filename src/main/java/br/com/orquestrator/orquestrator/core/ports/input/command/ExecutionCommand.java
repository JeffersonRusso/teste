package br.com.orquestrator.orquestrator.core.ports.input.command;

import java.util.Collections;
import java.util.Map;

import static java.util.UUID.*;

/**
 * ExecutionCommand: Comando auto-validável de execução de pipeline.
 * Implementa o princípio Fail-Fast na fronteira do Core.
 */
public record ExecutionCommand(
    String operationType,
    String correlationId,
    String executionStrategy,
    Map<String, Object> payload
) {
    /**
     * Construtor compacto com validação intrínseca.
     */
    public ExecutionCommand {
        // Validação de Presença (Obrigatórios)
        if (operationType == null || operationType.isBlank()) {
            throw new IllegalArgumentException("O tipo de operação (operationType) é obrigatório para executar um pipeline.");
        }

        // Normalização de Estado (Garante que nunca haja nulls perigosos)
        if (payload == null) {
            payload = Collections.emptyMap();
        }
        
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = randomUUID().toString().substring(0, 8);
        }
    }

    /**
     * Fábrica estática para criação rápida (DSL).
     */
    public static ExecutionCommand of(String operationType, Map<String, Object> payload) {
        return new ExecutionCommand(operationType, null, null, payload);
    }
}
