package br.com.orquestrator.orquestrator.api.task;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import java.util.Map;

/**
 * TaskResult: Representa o resultado da execução de uma Task.
 * Agora desacoplado do Jackson, utilizando a abstração DataNode.
 */
public sealed interface TaskResult {
    record Success(DataNode body, Map<String, Object> metadata) implements TaskResult {}
    record Failure(String errorMessage, Integer errorCode) implements TaskResult {}
    record Skipped() implements TaskResult {}

    static TaskResult success(DataNode body) {
        return new Success(body, Map.of());
    }

    static TaskResult error(int code, String message) {
        return new Failure(message, code);
    }
}
