package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * TaskResult: Contrato único de saída para qualquer Task.
 * Exige DataValue para garantir a tipagem desde a origem.
 */
public record TaskResult(
    DataValue body,
    int status,
    Map<String, Object> metadata
) {
    public static TaskResult success(DataValue body) {
        return new TaskResult(body, 200, Map.of());
    }

    // Sobrecarga de conveniência para facilitar a criação (converte automaticamente)
    public static TaskResult success(Object body) {
        return new TaskResult(DataValue.of(body), 200, Map.of());
    }

    public static TaskResult success(DataValue body, Map<String, Object> metadata) {
        return new TaskResult(body, 200, metadata);
    }

    public static TaskResult error(int status, String message) {
        return new TaskResult(new DataValue.Empty(), status, Map.of("error_message", message));
    }

    public static TaskResult failure(Map<String, Object> errorBody) {
        return new TaskResult(DataValue.of(errorBody), 500, Map.of());
    }

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
