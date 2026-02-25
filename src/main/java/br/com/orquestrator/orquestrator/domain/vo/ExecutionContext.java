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
 * ExecutionContext: Otimizado para criação ultra-rápida (40k+/s).
 */
@Getter
public class ExecutionContext implements DataStore, ExecutionMonitor {

    private final String correlationId;
    private final String operationType;
    private final TraceContext trace;
    
    // OTIMIZAÇÃO: Tamanho inicial reduzido (16) e concurrencyLevel ajustado para o número real 
    // de threads paralelas em um DAG típico (8), economizando memória e tempo de inicialização.
    private final Map<String, Object> root = new ConcurrentHashMap<>(16, 0.75f, 8);
    private final Map<Class<?>, Object> attachments = new ConcurrentHashMap<>(4, 0.75f, 4);

    public ExecutionContext(String correlationId, String operationType, TraceContext trace, Map<String, Object> initialData) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.trace = trace;
        if (initialData != null && !initialData.isEmpty()) this.root.putAll(initialData);
    }

    @SuppressWarnings("unchecked")
    public <T> T computeAttachmentIfAbsent(Class<T> type, Function<ExecutionContext, T> factory) {
        T cached = (T) attachments.get(type);
        if (cached != null) return cached;
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
        if (value == null || !type.isInstance(value)) return Optional.empty();
        return Optional.of(type.cast(value));
    }

    @Override
    public void track(String nodeId, String key, Object value) {
        var span = trace.getSpan(nodeId).orElse(null);
        if (span != null) span.addMetadata(key, value);
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
        var span = trace.getSpan(nodeId).orElse(null);
        return span != null ? span.getMetadata().get(key) : null;
    }

    @Deprecated public ExecutionTracker getTracker() { return null; }
}
