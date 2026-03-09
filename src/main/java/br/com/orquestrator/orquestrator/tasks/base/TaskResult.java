package br.com.orquestrator.orquestrator.tasks.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.Map;

/**
 * TaskResult: Resultado da execução de uma tarefa.
 * Agora usa JsonNode.
 */
public record TaskResult(
    Status status,
    JsonNode body,
    Map<String, Object> metadata,
    String errorMessage
) {
    public enum Status { SUCCESS, ERROR, SKIPPED }

    public static TaskResult success(JsonNode body) {
        return new TaskResult(Status.SUCCESS, body != null ? body : MissingNode.getInstance(), Map.of(), null);
    }

    public static TaskResult success(JsonNode body, Map<String, Object> metadata) {
        return new TaskResult(Status.SUCCESS, body != null ? body : MissingNode.getInstance(), metadata, null);
    }

    public static TaskResult error(int code, String message) {
        return new TaskResult(Status.ERROR, MissingNode.getInstance(), Map.of("code", code), message);
    }
    
    public static TaskResult failure(Map<String, Object> errorData) {
        // CORREÇÃO: Usando pojoNode para converter o mapa
        return new TaskResult(Status.ERROR, JsonNodeFactory.instance.pojoNode(errorData), Map.of(), "Falha na execução");
    }

    public boolean isSuccess() { return status == Status.SUCCESS; }
}
