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
 * Utiliza a nova estrutura de ExecutionContext e ExecutionTracker.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskContextFactory {

    private final List<ContextInitializer> initializers;

    public ExecutionContext create(String operationType, Map<String, String> headers, JsonNode rawBody) {
        Objects.requireNonNull(operationType, "O tipo de operação (operationType) é obrigatório");

        // 1. Resolve Identificação (Correlation ID)
        String correlationId = ContextHolder.getCorrelationId()
                .orElseGet(() -> UUID.randomUUID().toString());

        // 2. Prepara Dados Iniciais
        Map<String, Object> initialData = buildInitialData(operationType, headers, rawBody);

        // 3. Instancia o Contexto com seus especialistas
        ExecutionContext context = new ExecutionContext(
                correlationId, 
                operationType, 
                new ExecutionTracker(), 
                initialData
        );

        // 4. Executa Inicializadores (Normalização, etc)
        runInitializers(context, operationType);

        log.debug(STR."Contexto criado: \{operationType} [Correlation: \{correlationId}]");
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
                log.error(STR."Falha no inicializador [\{initializer.getClass().getSimpleName()}]: \{e.getMessage()}");
            }
        });
    }
}
