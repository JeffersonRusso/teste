package br.com.orquestrator.orquestrator.domain.tracker;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ExecutionSpan: Representa um intervalo de execução de uma tarefa.
 * Segue o padrão OpenTelemetry.
 */
@Slf4j
public class ExecutionSpan implements AutoCloseable {

    private final TraceContext trace;
    private final String nodeId;
    private final String type;
    private final long startNano;
    
    @Getter
    private final Map<String, Object> metadata = new ConcurrentHashMap<>();
    
    private volatile boolean finished = false;
    @Setter
    private int status = 200;
    private String errorMessage = null;

    public ExecutionSpan(TraceContext trace, String nodeId, String type) {
        this.trace = trace;
        this.nodeId = nodeId;
        this.type = type;
        this.startNano = System.nanoTime();
    }

    public void addMetadata(String key, Object value) {
        if (value != null) metadata.put(key, value);
    }

    public void fail(Throwable t) {
        this.status = 500;
        this.errorMessage = (t.getMessage() != null) ? t.getMessage() : t.getClass().getSimpleName();
    }
    
    public NodeMetrics toMetrics() {
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
        return new NodeMetrics(
                nodeId,
                type,
                durationMs,
                status,
                errorMessage,
                Map.copyOf(metadata)
        );
    }

    @Override
    public void close() {
        if (finished) return;
        finished = true;
        trace.record(toMetrics());
    }
}
