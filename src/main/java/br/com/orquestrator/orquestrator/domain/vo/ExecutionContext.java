package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contexto de Execução Orquestrada (A Fachada Inteligente).
 * Gerencia o estado da pipeline e coordena os subsistemas de rastro e tempo.
 * Java 21: Refatorado para maior segurança de tipos, imutabilidade e visibilidade entre Virtual Threads.
 */
@Slf4j
public class ExecutionContext {

    private final Map<String, Object> dataStore;
    
    @Getter
    private final String correlationId;
    
    @Getter
    private final String operationType;
    
    @Getter
    private final ExecutionTracker tracker;
    
    // Volatile garante que mudanças no deadline sejam visíveis entre Virtual Threads (Carrier Threads)
    private volatile ExecutionRuntime runtime;

    public ExecutionContext(@NonNull String correlationId,
                            @NonNull String operationType, 
                            @NonNull ExecutionTracker tracker,
                            @NonNull Map<String, Object> initialData) {
        this.correlationId = Objects.requireNonNull(correlationId);
        this.operationType = Objects.requireNonNull(operationType);
        this.tracker = Objects.requireNonNull(tracker);
        this.dataStore = new ConcurrentHashMap<>(initialData);
        this.runtime = new ExecutionRuntime(Instant.MAX);
    }

    /**
     * Atualiza o limite temporal da execução.
     */
    public void setDeadline(@NonNull Instant deadline) {
        this.runtime = new ExecutionRuntime(deadline);
    }

    public ExecutionRuntime runtime() {
        return this.runtime;
    }

    /**
     * Encaminha metadados de rastro diretamente para o tracker (Fachada).
     */
    public void trackTaskAction(String nodeId, String key, Object value) {
        tracker.getSpan(nodeId).ifPresent(span -> span.addMetadata(key, value));
    }

    /**
     * Atalho para compatibilidade com rastro de metadados.
     */
    public void addTaskMetadata(String nodeId, String key, Object value) {
        trackTaskAction(nodeId, key, value);
    }

    // --- Gerenciamento de Dados ---

    public void put(String key, Object value) {
        if (key != null && value != null) {
            this.dataStore.put(key, value);
        }
    }

    /**
     * Recupera um dado tipado, prevenindo ClassCastException externa.
     * Java 21: Utiliza Pattern Matching e Optional para segurança.
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(dataStore.get(key))
                .filter(type::isInstance)
                .map(type::cast);
    }

    public Object get(String key) {
        return this.dataStore.get(key);
    }

    public boolean has(String key) {
        return this.dataStore.containsKey(key);
    }

    /**
     * Retorna uma visão imutável dos dados atuais.
     */
    public Map<String, Object> readOnlyData() {
        return Map.copyOf(dataStore);
    }
    
    public Map<String, Object> getDataStore() {
        return readOnlyData();
    }
}
