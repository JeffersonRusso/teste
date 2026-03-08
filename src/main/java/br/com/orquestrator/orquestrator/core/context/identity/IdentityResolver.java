package br.com.orquestrator.orquestrator.core.context.identity;

import br.com.orquestrator.orquestrator.core.context.OperationTypeResolver;
import br.com.orquestrator.orquestrator.core.context.tag.TagManager;
import br.com.orquestrator.orquestrator.domain.ApiConstants;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * IdentityResolver: Resolve a identidade soberana da requisição.
 * Agora integra a resolução de tags para seleção dinâmica de pipelines.
 */
@Component
@RequiredArgsConstructor
public class IdentityResolver {

    private final CorrelationIdResolver correlationIdResolver;
    private final OperationTypeResolver operationTypeResolver;
    private final TagManager tagManager;
    private final IdGenerator idGenerator;

    public RequestIdentity resolve(Map<String, String> headers, Map<String, Object> body) {
        String orderId = Optional.ofNullable(body)
                .map(b -> b.get(ApiConstants.BODY_ORDER_ID))
                .map(Object::toString)
                .filter(id -> !id.isBlank())
                .orElseThrow(() -> new PipelineValidationException("O campo '" + ApiConstants.BODY_ORDER_ID + "' é obrigatório."));

        // Resolve as tags dinâmicas baseadas nos headers e no corpo da requisição
        Set<String> tags = tagManager.resolve(headers, body);

        return new RequestIdentity(
            correlationIdResolver.resolve(headers),
            operationTypeResolver.resolve(headers, body),
            orderId,
            idGenerator.generateFastId(),
            tags
        );
    }
}
