package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.ContextIdentity;
import br.com.orquestrator.orquestrator.core.context.storage.DataStore;
import br.com.orquestrator.orquestrator.core.context.validation.Constraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ExecutionContext: O Banco de Dados interno.
 * Implementa as visões delegando para os especialistas.
 */
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ExecutionContext implements ReadableContext, WriteableContext, ContextMetadata {
    
    private final ContextIdentity identity;
    private final DataStore storage;
    private final List<Constraint> constraints = new CopyOnWriteArrayList<>();

    // --- Implementação de ReadableContext ---
    @Override public Object get(String key) { return storage.get(key); }
    @Override public Map<String, Object> getRoot() { return storage.getAll(); }

    // --- Implementação de WriteableContext ---
    @Override 
    public void put(String key, Object value) {
        constraints.forEach(c -> c.validate(key, value, storage.getAll()));
        storage.put(key, value);
    }
    @Override public void addTag(String tag) { identity.addTag(tag); }

    // --- Implementação de ContextMetadata ---
    @Override public String getCorrelationId() { return identity.getCorrelationId(); }
    @Override public String getOperationType() { return identity.getOperationType(); }
    @Override public Set<String> getTags() { return identity.getTags(); }

    // --- Métodos de Visão ---
    public ReadableContext reader() { return this; }
    public WriteableContext writer() { return this; }
    public ContextMetadata metadata() { return this; }
}
