package br.com.orquestrator.orquestrator.core.context.identity;

import br.com.orquestrator.orquestrator.infra.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * CorrelationIdResolver: Especialista em rastreabilidade.
 * Segue a RFC 9110 e suporta W3C Trace Context para interoperabilidade.
 */
@Component
@RequiredArgsConstructor
public class CorrelationIdResolver {

    private final IdGenerator idGenerator;

    /**
     * Resolve o ID de correlação.
     * Prioridade: 
     * 1. W3C traceparent (Padrão Moderno)
     * 2. x-correlation-id (Legado Comum)
     * 3. Novo ID gerado
     */
    public String resolve(Map<String, String> headers) {
        if (headers == null) return idGenerator.generateFastId();

        return Optional.ofNullable(headers.get("traceparent")) // W3C Standard
                .or(() -> Optional.ofNullable(headers.get("x-correlation-id")))
                .or(() -> Optional.ofNullable(headers.get("correlation-id")))
                .filter(id -> !id.isBlank())
                .map(this::extractPureId)
                .orElseGet(idGenerator::generateFastId);
    }

    private String extractPureId(String rawId) {
        // Se for W3C (00-traceid-spanid-flags), extrai apenas o traceid
        if (rawId.contains("-") && rawId.split("-").length >= 4) {
            return rawId.split("-")[1];
        }
        return rawId;
    }
}
