package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Fábrica responsável pela criação e inicialização do contexto de execução.
 * Agora utiliza ContextHolder (ScopedValue) para obter o Correlation ID.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskContextFactory {

    private final List<ContextInitializer> initializers;

    public ExecutionContext create(String operationType, Map<String, String> headers, JsonNode rawBody) {
        Objects.requireNonNull(operationType, "O tipo de operação (operationType) é obrigatório");

        String correlationId = ContextHolder.getCorrelationId()
                .orElseGet(() -> UUID.randomUUID().toString());

        Map<String, Object> initialData = buildInitialData(operationType, headers, rawBody);
        
        ExecutionContext context = new ExecutionContext(correlationId, initialData, new ExecutionTracker());

        runInitializers(context, operationType);

        log.debug("Contexto criado: {} [Correlation: {}]", operationType, correlationId);
        return context;
    }

    private Map<String, Object> buildInitialData(String operationType, Map<String, String> headers, JsonNode rawBody) {
        return Map.of(
            ContextKey.RAW, Objects.requireNonNullElseGet(rawBody, JsonNodeFactory.instance::objectNode),
            ContextKey.HEADER, Objects.requireNonNullElse(headers, Collections.emptyMap()),
            ContextKey.OPERATION_TYPE, operationType
        );
    }

    private void runInitializers(ExecutionContext context, String operationType) {
        initializers.forEach(initializer -> {
            try {
                initializer.initialize(context, operationType);
            } catch (Exception e) {
                log.error("Falha no inicializador [{}]: {}", initializer.getClass().getSimpleName(), e.getMessage());
            }
        });
    }
}
