package br.com.orquestrator.orquestrator.tasks.base;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/**
 * Task: O contrato atômico de processamento.
 * Agora usa JsonNode puro.
 */
@FunctionalInterface
public interface Task {
    TaskResult execute(Map<String, JsonNode> inputs);
}
