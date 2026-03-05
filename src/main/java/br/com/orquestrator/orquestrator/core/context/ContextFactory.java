package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.ContextIdentity;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.context.storage.MapDataStore;
import br.com.orquestrator.orquestrator.domain.ApiConstants;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContextFactory {

    @SuppressWarnings("unchecked")
    public ExecutionContext create(RequestIdentity requestIdentity, Map<String, String> headers, Map<String, Object> fullBody) {
        var identity = new ContextIdentity(requestIdentity.correlationId(), requestIdentity.operationType());
        var storage = new MapDataStore();
        
        ExecutionContext context = new ExecutionContext(identity, storage);
        
        // Extrai apenas os dados de negócio (campo 'operation') para o namespace 'raw'
        Map<String, Object> operationData = (Map<String, Object>) fullBody.getOrDefault(ApiConstants.BODY_OPERATION_DATA, Map.of());

        storage.put(ContextSchema.header(), safeConcurrentMap(headers));
        storage.put(ContextSchema.raw(), safeConcurrentMap(operationData));
        storage.put(ContextSchema.standard(), new ConcurrentHashMap<>());
        
        storage.put(ContextKey.OPERATION_TYPE, requestIdentity.operationType());
        storage.put("order_id", requestIdentity.orderId());
        storage.put("execution_id", requestIdentity.executionId());

        addDefaultConstraints(context);

        return context;
    }

    private <K, V> Map<K, V> safeConcurrentMap(Map<K, V> source) {
        return source != null ? new ConcurrentHashMap<>(source) : new ConcurrentHashMap<>();
    }

    private void addDefaultConstraints(ExecutionContext context) {
        context.getConstraints().add((key, value, current) -> {
            if (current.containsKey(key) && (ContextKey.RAW.equals(key) || ContextKey.OPERATION_TYPE.equals(key))) {
                throw new PipelineException("Violação de Integridade: A chave '" + key + "' é protegida.");
            }
        });
    }
}
