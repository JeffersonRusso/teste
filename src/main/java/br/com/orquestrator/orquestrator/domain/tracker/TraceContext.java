package br.com.orquestrator.orquestrator.domain.tracker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TraceContext: Gerencia o rastro de execução da pipeline.
 * Otimizado para alta concorrência.
 */
@Slf4j
public class TraceContext {

    private final long startNano = System.nanoTime();
    private final Instant startTime = Instant.now(); // Mantido para metadados de evento
    private long endNano;

    private final Map<String, ExecutionSpan> activeSpans = new ConcurrentHashMap<>(64);
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
        this.endNano = System.nanoTime();
        if (!activeSpans.isEmpty()) {
            activeSpans.clear();
        }
    }

    public List<NodeMetrics> getMetrics() {
        return new ArrayList<>(history);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public long getDurationMs() {
        long end = (endNano != 0) ? endNano : System.nanoTime();
        return (end - startNano) / 1_000_000;
    }
}
