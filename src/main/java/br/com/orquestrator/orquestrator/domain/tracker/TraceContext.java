package br.com.orquestrator.orquestrator.domain.tracker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TraceContext: Gerencia o rastro de execução da pipeline.
 * Segue o padrão OpenTelemetry de Spans.
 */
@Slf4j
public class TraceContext {

    @Getter
    private final Instant startTime = Instant.now();
    private volatile Instant endTime;

    private final Map<String, ExecutionSpan> activeSpans = new ConcurrentHashMap<>();
    private final Queue<NodeMetrics> history = new ConcurrentLinkedQueue<>();

    public ExecutionSpan startSpan(String nodeId, String type) {
        ExecutionSpan span = new ExecutionSpan(this, nodeId, type);
        activeSpans.put(nodeId, span);
        return span;
    }

    public Optional<ExecutionSpan> getSpan(String nodeId) {
        return Optional.ofNullable(activeSpans.get(nodeId));
    }

    public void record(NodeMetrics metrics) {
        activeSpans.remove(metrics.nodeId());
        history.add(metrics);
    }

    public void finish() {
        this.endTime = Instant.now();
        if (!activeSpans.isEmpty()) {
            log.warn(STR."Trace finalizado com \{activeSpans.size()} spans órfãos.");
            activeSpans.clear();
        }
    }

    public List<NodeMetrics> getMetrics() {
        return List.copyOf(history);
    }

    public long getDurationMs() {
        Instant end = (endTime != null) ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }
}
