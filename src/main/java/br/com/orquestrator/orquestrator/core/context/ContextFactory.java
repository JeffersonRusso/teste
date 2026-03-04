package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.ContextIdentity;
import br.com.orquestrator.orquestrator.core.context.storage.MapDataStore;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContextFactory {

    private final IdGenerator idGenerator;
    private final ExpressionEngine expressionEngine;

    public ExecutionContext create(String operationType, Map<String, String> headers, Map<String, Object> rawBody) {
        String correlationId = resolveCorrelationId(headers);
        
        var identity = new ContextIdentity(correlationId, operationType);
        var storage = new MapDataStore();
        
        ExecutionContext context = new ExecutionContext(identity, storage);
        
        // Usa o SCHEMA para inicializar os namespaces
        storage.put(ContextSchema.header(), headers != null ? new ConcurrentHashMap<>(headers) : new ConcurrentHashMap<>());
        storage.put(ContextSchema.raw(), rawBody != null ? new ConcurrentHashMap<>(rawBody) : new ConcurrentHashMap<>());
        storage.put(ContextSchema.standard(), new ConcurrentHashMap<>());
        
        storage.put(ContextKey.OPERATION_TYPE, operationType);

        addDefaultConstraints(context);

        return context;
    }

    private void addDefaultConstraints(ExecutionContext context) {
        context.getConstraints().add((key, value, current) -> {
            if (current.containsKey(key) && (ContextKey.RAW.equals(key) || ContextKey.OPERATION_TYPE.equals(key))) {
                throw new PipelineException("Violação de Integridade: A chave '" + key + "' é protegida.");
            }
        });
    }

    private String resolveCorrelationId(Map<String, String> headers) {
        return Optional.ofNullable(headers)
                .map(h -> h.get("x-correlation-id"))
                .or(() -> Optional.ofNullable(headers).map(h -> h.get("correlation-id")))
                .filter(id -> !id.isBlank())
                .orElseGet(idGenerator::generateFastId);
    }
}
