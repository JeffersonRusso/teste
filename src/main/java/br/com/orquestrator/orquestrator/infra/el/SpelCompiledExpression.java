package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * SpelCompiledExpression: Execução pura e soberana.
 */
@Slf4j
@RequiredArgsConstructor
public final class SpelCompiledExpression implements CompiledExpression {

    private final Expression expression;
    private final SpelContextFactory contextFactory;

    @Override
    public <T> T evaluate(Object root, Class<T> targetType) {
        // A fábrica já cuida de garantir que root seja um DataNode
        EvaluationContext context = contextFactory.create(root);
        try {
            return expression.getValue(context, targetType);
        } catch (Exception e) {
            log.error("FALHA SpEL [{}]: {}", expression.getExpressionString(), e.getMessage());
            throw new RuntimeException("Erro ao processar lógica dinâmica: " + expression.getExpressionString(), e);
        }
    }
}
