package br.com.orquestrator.orquestrator.domain.tracker;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Representa um intervalo de execução de uma tarefa (Span).
 * Acumula dados localmente e reporta ao Tracker ao finalizar.
 */
@Slf4j
public class ExecutionSpan implements AutoCloseable {

    private final ExecutionTracker tracker;
    private final String nodeId;
    private final String type;
    private final Instant startTime;
    private final long startNano;
    
    private final Map<String, Object> inputs = new ConcurrentHashMap<>();
    private final Map<String, Object> outputs = new ConcurrentHashMap<>();
    private final Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    private volatile boolean finished = false;
    @Setter
    private String status = "COMPLETED"; // Default se fechar sem success/fail explícito
    private String errorMessage = null;

    public ExecutionSpan(ExecutionTracker tracker, String nodeId, String type) {
        this.tracker = tracker;
        this.nodeId = nodeId;
        this.type = type;
        this.startTime = Instant.now();
        this.startNano = System.nanoTime();
    }

    public void addInput(String key, Object value) {
        if (value != null) inputs.put(key, value);
    }

    public void addOutput(String key, Object value) {
        if (value != null) outputs.put(key, value);
    }

    public void addMetadata(String key, Object value) {
        if (value != null) metadata.put(key, value);
    }

    public void success() {
        this.status = "SUCCESS";
    }

    public void fail(Throwable t) {
        this.status = "FAILED";
        this.errorMessage = (t.getMessage() != null) ? t.getMessage() : t.getClass().getSimpleName();
    }
    
    public boolean hasMetadata(String key) {
        return metadata.containsKey(key);
    }
    
    public NodeMetrics toMetrics() {
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
        return new NodeMetrics(
                nodeId,
                type,
                startTime,
                Instant.now(),
                status,
                errorMessage,
                durationMs,
                Map.copyOf(inputs),
                Map.copyOf(outputs),
                Map.copyOf(metadata)
        );
    }

    @Override
    public void close() {
        if (finished) return;
        finished = true;

        NodeMetrics metrics = toMetrics();
        tracker.record(metrics);
    }
}
