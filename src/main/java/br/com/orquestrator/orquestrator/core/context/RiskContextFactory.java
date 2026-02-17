package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ContextBuilder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializer;
import br.com.orquestrator.orquestrator.infra.cache.GlobalDataCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * RiskContextFactory: Orquestra a criação do contexto usando SOLID.
 */
@Component
@RequiredArgsConstructor
public class RiskContextFactory {

    private final List<ContextInitializer> initializers;
    private final ObjectMapper objectMapper;
    private final GlobalDataCache globalCache;

    public ExecutionContext create(String operationType, Map<String, String> headers, JsonNode rawBody) {
        // 1. Montagem inicial via Builder
        ExecutionContext context = ContextBuilder.init(operationType)
                .withCorrelationId(ContextHolder.getCorrelationId().orElse(null))
                .withAllData(globalCache.getAll())
                .withData(ContextKey.HEADER, headers)
                .withData(ContextKey.RAW, parseBody(rawBody))
                .build();
        
        // 2. Inicialização via Pipeline (OCP)
        initializers.forEach(i -> i.initialize(context, operationType));

        return context;
    }

    private Map<String, Object> parseBody(JsonNode body) {
        if (body == null || body.isMissingNode()) return Map.of();
        return objectMapper.convertValue(body, Map.class);
    }
}
