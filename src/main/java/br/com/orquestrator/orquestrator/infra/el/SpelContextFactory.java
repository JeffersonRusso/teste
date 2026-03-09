package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * SpelContextFactory: Cria o contexto de avaliação SpEL.
 * Otimizado para JsonNode.
 */
@Component
@RequiredArgsConstructor
public class SpelContextFactory {

    private final MapAccessor mapAccessor = new MapAccessor();
    private final JsonNodeAccessor jsonNodeAccessor;

    public StandardEvaluationContext create(Object root) {
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        context.addPropertyAccessor(jsonNodeAccessor);
        context.addPropertyAccessor(mapAccessor);
        return context;
    }
}
