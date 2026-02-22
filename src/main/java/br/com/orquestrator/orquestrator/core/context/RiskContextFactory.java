package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.port.in.PrepareContextUseCase;
import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ContextBuilder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RiskContextFactory: Fábrica de contexto otimizada.
 * Otimizado para evitar o lock do SecureRandom (UUID.randomUUID).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskContextFactory implements PrepareContextUseCase {

    private final ContextNormalizer contextNormalizer;
    private final IdGenerator idGenerator;

    @Override
    public ExecutionContext execute(String operationType, Map<String, String> headers, Map<String, Object> rawBody) {
        // 1. Criação base do contexto
        ExecutionContext context = ContextBuilder.init(operationType)
                .withCorrelationId(extractCorrelationId(headers))
                .withData(ContextKey.HEADER, headers)
                .withData(ContextKey.RAW, rawBody) 
                .build();

        // 2. Normalização estática
        contextNormalizer.normalize(context, operationType);

        return context;
    }

    private String extractCorrelationId(Map<String, String> headers) {
        if (headers == null) return idGenerator.generateFastId();
        
        String correlationId = headers.get("x-correlation-id");
        if (correlationId == null) {
            correlationId = headers.get("correlation-id");
        }
        
        // Se não houver ID no header, gera um ID rápido (Lock-free)
        return (correlationId != null && !correlationId.isBlank()) ? correlationId : idGenerator.generateFastId();
    }
}
