package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.context.identity.ContextIdentity;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.context.storage.MapDataStore;
import br.com.orquestrator.orquestrator.domain.ApiConstants;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ContextFactory {

    @SuppressWarnings("unchecked")
    public ExecutionContext create(
            RequestIdentity requestIdentity,
            Map<String, String> headers,
            Map<String, Object> fullBody) {
        var identity = new ContextIdentity(requestIdentity.correlationId(), requestIdentity.operationType());
        var storage = new MapDataStore();
        
        ExecutionContext context = new ExecutionContext(identity, storage);
        
        Map<String, Object> operationData =
                (Map<String, Object>) fullBody.getOrDefault(ApiConstants.BODY_OPERATION_DATA, Map.of());

        WriteableContext writer = context.writer();
        
        // CRÍTICO: Usar ConcurrentHashMap para garantir mutabilidade nos namespaces
        writer.put(ContextSchema.header(), DataValue.of(new ConcurrentHashMap<>(headers)));
        writer.put(ContextSchema.raw(), DataValue.of(new ConcurrentHashMap<>(operationData)));
        writer.put(ContextSchema.standard(), DataValue.of(new ConcurrentHashMap<>()));
        
        writer.put(ContextKey.OPERATION_TYPE, DataValue.of(requestIdentity.operationType()));
        writer.put("order_id", DataValue.of(requestIdentity.orderId()));
        writer.put("execution_id", DataValue.of(requestIdentity.executionId()));

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
}
