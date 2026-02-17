package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.tracker.TraceContext;
import br.com.orquestrator.orquestrator.infra.json.PathNavigator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExecutionContext: Documento Vivo, Lock-free e otimizado para Virtual Threads.
 */
@Getter
public class ExecutionContext implements DataStore, ExecutionMonitor {

    private final String correlationId;
    private final String operationType;
    private final TraceContext trace;
    private final Map<String, Object> root = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public ExecutionContext(String correlationId, String operationType, TraceContext trace, Map<String, Object> initialData) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.trace = trace;
        if (initialData != null) this.root.putAll(initialData);
    }

    @Override
    public void put(String path, Object value) {
        PathNavigator.write(root, path, value);
    }

    @Override
    public Object get(String path) {
        return PathNavigator.read(root, path);
    }

    @Override
    public void track(String nodeId, String key, Object value) {
        trace.getSpan(nodeId).ifPresent(span -> span.addMetadata(key, value));
    }

    @Override
    public void setStatus(String nodeId, int code) {
        track(nodeId, "status", code);
    }

    @Override
    public void setError(String nodeId, Object error) {
        track(nodeId, "error", error);
        put(STR."errors.\{nodeId}", error);
    }

    @Override
    public Object getMeta(String nodeId, String key) {
        return trace.getSpan(nodeId)
                .map(s -> s.getMetadata().get(key))
                .orElse(null);
    }

    @Deprecated public ExecutionTracker getTracker() { return null; }
}
