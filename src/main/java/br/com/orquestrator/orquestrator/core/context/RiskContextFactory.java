package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ContextBuilder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * RiskContextFactory: Fábrica de contexto pura.
 * Apenas cria o objeto base. A normalização agora é feita pelo orquestrador.
 */
@Component
@RequiredArgsConstructor
public class RiskContextFactory {

    private final IdGenerator idGenerator;

    public ExecutionContext execute(String operationType, Map<String, String> headers, Map<String, Object> rawBody) {
        String correlationId = resolveCorrelationId(headers);

        return ContextBuilder.init(operationType)
                .withCorrelationId(correlationId)
                .withData(ContextKey.HEADER, headers)
                .withData(ContextKey.RAW, rawBody)
                .build();
    }

    private String resolveCorrelationId(Map<String, String> headers) {
        return Optional.ofNullable(headers)
                .map(h -> h.get("x-correlation-id"))
                .or(() -> Optional.ofNullable(headers).map(h -> h.get("correlation-id")))
                .filter(id -> !id.isBlank())
                .orElseGet(idGenerator::generateFastId);
    }
}
