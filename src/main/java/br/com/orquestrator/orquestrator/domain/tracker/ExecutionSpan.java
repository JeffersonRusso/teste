package br.com.orquestrator.orquestrator.domain.tracker;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ExecutionSpan: Representa um intervalo de execução de uma tarefa.
 * Otimizado: Usa HashMap simples pois cada Span é manipulado por uma única Virtual Thread.
 */
@Slf4j
public class ExecutionSpan implements AutoCloseable {

    private final TraceContext trace;
    private final String nodeId;
    private final String type;
    private final long startNano;
    
    // Otimização: HashMap simples é muito mais rápido que ConcurrentHashMap para acesso single-thread
    private final Map<String, Object> metadata = new HashMap<>(8);
    
    private boolean finished = false;
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void fail(Throwable t) {
        this.status = 500;
        this.errorMessage = (t.getMessage() != null) ? t.getMessage() : t.getClass().getSimpleName();
    }
    
    public NodeMetrics toMetrics() {
        // Conversão manual de nano para milli para evitar overhead de TimeUnit
        long durationMs = (System.nanoTime() - startNano) / 1_000_000;
        return new NodeMetrics(
                nodeId,
                type,
                durationMs,
                status,
                errorMessage,
                metadata.isEmpty() ? Map.of() : new HashMap<>(metadata)
        );
    }

    @Override
    public void close() {
        if (finished) return;
        finished = true;
        trace.record(toMetrics());
    }
}
