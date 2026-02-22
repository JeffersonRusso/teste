package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.tracker.TraceContext;
import br.com.orquestrator.orquestrator.infra.json.PathNavigator;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * ExecutionContext: Documento Vivo, Lock-free e otimizado para Virtual Threads.
 */
@Getter
public class ExecutionContext implements DataStore, ExecutionMonitor {

    private final String correlationId;
    private final String operationType;
    private final TraceContext trace;
    private final Map<String, Object> root = new ConcurrentHashMap<>();
    
    // Cache para objetos pesados (como o EvaluationContext do SpEL)
    private final Map<Class<?>, Object> attachments = new ConcurrentHashMap<>();

    public ExecutionContext(String correlationId, String operationType, TraceContext trace, Map<String, Object> initialData) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.trace = trace;
        if (initialData != null) this.root.putAll(initialData);
    }

    @SuppressWarnings("unchecked")
    public <T> T computeAttachmentIfAbsent(Class<T> type, Function<ExecutionContext, T> factory) {
        return (T) attachments.computeIfAbsent(type, _ -> factory.apply(this));
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
    public <T> Optional<T> get(String path, Class<T> type) {
        Object value = get(path);
        return Optional.ofNullable(value)
                .filter(type::isInstance)
                .map(type::cast);
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
