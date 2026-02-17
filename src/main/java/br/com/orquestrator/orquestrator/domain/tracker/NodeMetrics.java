package br.com.orquestrator.orquestrator.domain.tracker;

import java.util.Map;

/**
 * NodeMetrics: Snapshot imutável da execução de uma task.
 */
public record NodeMetrics(
    String nodeId,
    String type,
    long durationMs,
    int status,
    String errorMessage,
    Map<String, Object> metadata
) {}
