package br.com.orquestrator.orquestrator.domain.event;

import br.com.orquestrator.orquestrator.domain.tracker.NodeMetrics;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Snapshot consolidado de uma execução para exportação (S3, Kafka, etc).
 */
public record
PipelineExecutionSummary(
    String correlationId,
    String operationType,
    Instant startTime,
    long totalDurationMs,
    List<NodeMetrics> nodeMetrics,
    Map<String, Object> finalContextData,
    boolean success
) {}
