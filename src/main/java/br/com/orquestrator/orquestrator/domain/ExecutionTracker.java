package br.com.orquestrator.orquestrator.domain;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.tracker.NodeMetrics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ExecutionTracker {

    @Getter
    private final Instant startTime;
    private Instant endTime;

    // ConcurrentLinkedQueue é non-blocking e muito mais eficiente para muitas escritas
    private final Queue<NodeMetrics> nodes = new ConcurrentLinkedQueue<>();

    public ExecutionTracker() {
        this.startTime = Instant.now();
    }

    public ExecutionSpan start(String nodeId, String type) {
        return new ExecutionSpan(this, nodeId, type);
    }

    public void record(NodeMetrics metrics) {
        nodes.add(metrics);
        logNodeCompletion(metrics);
    }

    private void logNodeCompletion(NodeMetrics metrics) {
        if ("FAILED".equals(metrics.status())) {
            log.error("Task [{}] falhou em {}ms. Erro: {}", metrics.nodeId(), metrics.durationMs(), metrics.errorMessage());
        } else {
            log.info("Task [{}] concluída em {}ms. Status: {}", metrics.nodeId(), metrics.durationMs(), metrics.status());
        }
    }

    public void finish() {
        this.endTime = Instant.now();
    }

    public List<NodeMetrics> getMetrics() {
        // Retorna uma cópia imutável (snapshot)
        return List.copyOf(nodes);
    }

    public long getTotalDurationMs() {
        Instant end = (endTime != null) ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }
}
