package br.com.orquestrator.orquestrator.application.usecase;

import java.util.Map;

/**
 * PipelineExecutionResult: Objeto de resultado rico e padronizado.
 */
public record PipelineExecutionResult(
    String executionId,
    String operationType,
    Map<String, Object> output,
    long durationMs,
    boolean success,
    String message
) {
    public static PipelineExecutionResult success(String id, String op, Map<String, Object> data, long duration) {
        return new PipelineExecutionResult(id, op, data, duration, true, "Execução concluída com sucesso");
    }

    public static PipelineExecutionResult failure(String id, String op, String error, long duration) {
        return new PipelineExecutionResult(id, op, Map.of(), duration, false, error);
    }
}
