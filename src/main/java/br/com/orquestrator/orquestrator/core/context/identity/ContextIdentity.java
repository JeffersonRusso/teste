package br.com.orquestrator.orquestrator.core.context.identity;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public record ContextIdentity(
    String correlationId,
    String operationType,
    Set<String> tags
) implements ContextMetadata {
    
    public ContextIdentity(String correlationId, String operationType) {
        this(correlationId, operationType, ConcurrentHashMap.newKeySet());
        this.tags.add("default");
    }

    public void addTag(String tag) { if (tag != null) this.tags.add(tag); }
    
    @Override public String getCorrelationId() { return correlationId; }
    @Override public String getOperationType() { return operationType; }
    @Override public Set<String> getTags() { return Collections.unmodifiableSet(tags); }
}
