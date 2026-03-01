package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * O "Vinho": Contrato único de saída para qualquer Task.
 * @param body O dado de negócio (Tipado via DataValue).
 * @param status Código de status (ex: 200, 404, 500).
 * @param metadata Informações extras para o tracker (métricas, logs).
 */
public record TaskResult(
    DataValue body,
    int status,
    Map<String, Object> metadata
) {
    public static TaskResult success(Object body) {
        return new TaskResult(DataValue.of(body), 200, Map.of());
    }

    public static TaskResult success(Object body, Map<String, Object> metadata) {
        return new TaskResult(DataValue.of(body), 200, metadata);
    }

    public static TaskResult error(int status, String message) {
        return new TaskResult(new DataValue.Empty(), status, Map.of("error_message", message));
    }

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
