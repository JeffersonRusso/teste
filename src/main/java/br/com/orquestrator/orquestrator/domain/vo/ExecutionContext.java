package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.Map;

/**
 * Contexto de Execução: Um Documento JSON Vivo.
 */
@Getter
public class ExecutionContext {

    public static final String STATUS = "status";
    public static final String BODY = "body";
    public static final String ERROR = "error";

    private final String correlationId;
    private final String operationType;
    private final ExecutionTracker tracker;
    private final ObjectNode root = JsonNodeFactory.instance.objectNode();
    private static final ObjectMapper mapper = new ObjectMapper();

    public ExecutionContext(String correlationId, String operationType, ExecutionTracker tracker, Map<String, Object> initialData) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.tracker = tracker;
        if (initialData != null) {
            initialData.forEach(this::put);
        }
    }

    public synchronized void put(String path, Object value) {
        if (path == null || value == null) return;
        
        String[] segments = path.split("\\.");
        ObjectNode current = root;
        
        for (int i = 0; i < segments.length - 1; i++) {
            current = current.withObject("/" + segments[i]);
        }
        current.set(segments[segments.length - 1], mapper.valueToTree(value));
    }

    public JsonNode get(String path) {
        if (path == null) return null;
        JsonNode node = root.at("/" + path.replace(".", "/"));
        return node.isMissingNode() ? null : node;
    }

    public void track(String nodeId, String key, Object value) {
        tracker.getSpan(nodeId).ifPresent(span -> span.addMetadata(key, value));
    }

    public void setStatus(String nodeId, int code) { track(nodeId, STATUS, code); }
    
    public Object getMeta(String nodeId, String key) {
        return tracker.getSpan(nodeId)
                .map(s -> s.toMetrics().metadata().get(key))
                .orElse(null);
    }

    // --- API de Compatibilidade ---
    public Map<String, Object> asMap() { return mapper.convertValue(root, Map.class); }
    @Deprecated public Map<String, Object> readOnlyData() { return asMap(); }
    @Deprecated public Map<String, Object> getDataStore() { return asMap(); }
    @Deprecated public void trackTaskAction(String nodeId, String key, Object value) { track(nodeId, key, value); }
    @Deprecated public void setMeta(String nodeId, String key, Object value) { track(nodeId, key, value); }
}
