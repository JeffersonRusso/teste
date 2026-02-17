package br.com.orquestrator.orquestrator.tasks.base;

import java.util.Map;

/**
 * O "Vinho": Contrato único de saída para qualquer Task.
 * @param body O dado de negócio (JSON).
 * @param status Código de status (ex: 200, 404, 500).
 * @param metadata Informações extras para o tracker (métricas, logs).
 */
public record TaskResult(
    Object body,
    int status,
    Map<String, Object> metadata
) {
    public static TaskResult success(Object body) {
        return new TaskResult(body, 200, Map.of());
    }

    public static TaskResult success(Object body, Map<String, Object> metadata) {
        return new TaskResult(body, 200, metadata);
    }

    public static TaskResult error(int status, String message) {
        return new TaskResult(null, status, Map.of("error_message", message));
    }
}
