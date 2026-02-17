package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializer;
import br.com.orquestrator.orquestrator.infra.cache.GlobalDataCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Fábrica de Contexto: Unifica dados globais e locais.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskContextFactory {

    private final List<ContextInitializer> initializers;
    private final ObjectMapper objectMapper;
    private final GlobalDataCache globalCache;

    public ExecutionContext create(String operationType, Map<String, String> headers, JsonNode rawBody) {
        String correlationId = ContextHolder.getCorrelationId().orElseGet(() -> UUID.randomUUID().toString());

        // 1. Inicia com dados globais (ex: tokens)
        Map<String, Object> initialData = new HashMap<>(globalCache.getAll());

        // 2. Adiciona dados da requisição
        Map<String, Object> bodyMap = rawBody != null ? objectMapper.convertValue(rawBody, Map.class) : new HashMap<>();
        initialData.put(ContextKey.RAW, bodyMap);
        initialData.put(ContextKey.HEADER, Objects.requireNonNullElse(headers, Collections.emptyMap()));
        initialData.put(ContextKey.OPERATION_TYPE, operationType);

        ExecutionContext context = new ExecutionContext(correlationId, operationType, new ExecutionTracker(), initialData);
        
        // 3. Roda inicializadores (Normalização, etc)
        runInitializers(context, operationType);

        return context;
    }

    private void runInitializers(ExecutionContext context, String operationType) {
        initializers.forEach(i -> i.initialize(context, operationType));
    }
}
