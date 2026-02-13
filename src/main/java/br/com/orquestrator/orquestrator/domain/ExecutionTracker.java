package br.com.orquestrator.orquestrator.domain;

import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import br.com.orquestrator.orquestrator.domain.tracker.NodeMetrics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Gerenciador de Rastro e Métricas (O Subsistema de Observabilidade).
 * Implementa um modelo de Spans ativos e histórico de métricas imutável.
 * Java 21: Utiliza ConcurrentHashMap e ConcurrentLinkedQueue para alta performance lock-free.
 */
@Slf4j
public class ExecutionTracker {

    @Getter
    private final Instant startTime;
    private volatile Instant endTime;

    // Spans que ainda estão sendo processados (em voo)
    private final Map<String, ExecutionSpan> activeSpans = new ConcurrentHashMap<>();
    
    // Histórico finalizado e thread-safe (Lock-free)
    private final Queue<NodeMetrics> metricsHistory = new ConcurrentLinkedQueue<>();

    public ExecutionTracker() {
        this.startTime = Instant.now();
    }

    /**
     * Cria e registra um novo Span de execução para um nó.
     */
    public ExecutionSpan startSpan(String nodeId, String type) {
        ExecutionSpan span = new ExecutionSpan(this, nodeId, type);
        activeSpans.put(nodeId, span);
        return span;
    }

    /**
     * Localiza um Span ativo. Usado pela fachada do Contexto.
     */
    public Optional<ExecutionSpan> getSpan(String nodeId) {
        return Optional.ofNullable(activeSpans.get(nodeId));
    }

    /**
     * Finaliza o ciclo de vida de um nó e arquiva suas métricas.
     * Chamado automaticamente pelo ExecutionSpan.close().
     */
    public void recordCompletion(NodeMetrics metrics) {
        activeSpans.remove(metrics.nodeId());
        metricsHistory.add(metrics);
        
        logNodeCompletion(metrics);
    }

    private void logNodeCompletion(NodeMetrics metrics) {
        if ("FAILED".equals(metrics.status())) {
            log.warn(STR."Node [\{metrics.nodeId()}]: FAILED em \{metrics.durationMs()}ms - \{metrics.errorMessage()}");
        } else {
            log.debug(STR."Node [\{metrics.nodeId()}]: SUCCESS em \{metrics.durationMs()}ms");
        }
    }

    /**
     * Encerra o rastreio global da pipeline.
     */
    public void finish() {
        this.endTime = Instant.now();
        if (!activeSpans.isEmpty()) {
            log.warn(STR."Tracker finalizado com \{activeSpans.size()} spans ainda ativos.");
            activeSpans.clear();
        }
    }

    public List<NodeMetrics> getFullMetrics() {
        return List.copyOf(metricsHistory);
    }

    public long getTotalTimeMs() {
        Instant end = (endTime != null) ? endTime : Instant.now();
        return Duration.between(startTime, end).toMillis();
    }

    // --- Métodos de compatibilidade ---
    public ExecutionSpan start(String nodeId, String type) { return startSpan(nodeId, type); }
    public void record(NodeMetrics metrics) { recordCompletion(metrics); }
    public List<NodeMetrics> getMetrics() { return getFullMetrics(); }
    public long getTotalDurationMs() { return getTotalTimeMs(); }
}
