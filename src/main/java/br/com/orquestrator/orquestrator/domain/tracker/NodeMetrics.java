package br.com.orquestrator.orquestrator.domain.tracker;

import java.time.Instant;
import java.util.Map;

/**
 * Snapshot imutável de métricas de um nó.
 */
public record NodeMetrics(
        String nodeId,
        String type,
        Instant startTime,
        Instant endTime,
        String status,
        String errorMessage,
        long durationMs,
        Map<String, Object> inputs,
        Map<String, Object> outputs,
        Map<String, Object> metadata
) {}
