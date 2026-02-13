package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.tracker.ExecutionSpan;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExecutionContext {

    private final Map<String, Object> dataStore;
    
    @Getter
    private final String correlationId;
    
    @Getter
    private final String operationType;
    
    @Getter
    private final ExecutionTracker tracker;
    
    @Getter
    private Instant deadline;

    public ExecutionContext(@Nullable String correlationId, 
                            @Nullable Map<String, Object> initialData, 
                            @Nullable ExecutionTracker tracker) {
        this.dataStore = new ConcurrentHashMap<>(initialData != null ? initialData : Collections.emptyMap());
        this.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        this.tracker = tracker;
        this.deadline = Instant.MAX;
        
        Object opType = this.dataStore.get(ContextKey.OPERATION_TYPE);
        this.operationType = opType != null ? opType.toString() : "UNKNOWN";
    }

    public void setDeadline(@NonNull Instant deadline) {
        this.deadline = Objects.requireNonNull(deadline, "Deadline cannot be null");
    }

    public long getRemainingTimeMs() {
        if (Instant.MAX.equals(deadline)) {
            return Long.MAX_VALUE;
        }
        try {
            long remaining = Duration.between(Instant.now(), deadline).toMillis();
            return Math.max(0, remaining);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }
    
    public long checkTimeBudget(long taskTimeout) {
        long remaining = getRemainingTimeMs();
        long effective = Math.min(taskTimeout, remaining);

        if (effective <= 0) {
            throw new IllegalStateException("Time Budget esgotado (Deadline: " + deadline + ")");
        }
        return effective;
    }
    
    /**
     * Adiciona metadados ao span de uma task específica.
     */
    public void addTaskMetadata(String nodeId, String key, Object value) {
        String spanKey = ContextKey.SPAN_PREFIX + nodeId;
        Object spanObj = this.dataStore.get(spanKey);
        
        if (spanObj instanceof ExecutionSpan span) {
            span.addMetadata(key, value);
        }
    }

    public void put(String key, Object value) {
        if (key != null && value != null) {
            this.dataStore.put(key, value);
        }
    }
    
    public void remove(String key) {
        if (key != null) {
            this.dataStore.remove(key);
        }
    }
    
    public void putAll(Map<String, Object> values) {
        if (values != null && !values.isEmpty()) {
            this.dataStore.putAll(values);
        }
    }

    public Object get(String key) {
        return this.dataStore.get(key);
    }

    public boolean has(String key) {
        return this.dataStore.containsKey(key);
    }

    public Map<String, Object> getDataStore() {
        return Collections.unmodifiableMap(dataStore);
    }
}
