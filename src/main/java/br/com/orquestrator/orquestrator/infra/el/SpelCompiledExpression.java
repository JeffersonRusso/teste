package br.com.orquestrator.orquestrator.infra.el;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * SpelCompiledExpression: Bolinha de lógica dinâmica baseada em SpEL.
 */
@RequiredArgsConstructor
public final class SpelCompiledExpression implements CompiledExpression {

    private final Expression expression;
    private final SpelContextFactory contextFactory;

    @Override
    public JsonNode evaluate(Object root) {
        EvaluationContext context = contextFactory.create(root);
        try {
            Object value = expression.getValue(context);
            if (value instanceof JsonNode jn) return jn;
            return JsonNodeFactory.instance.pojoNode(value);
        } catch (Exception e) {
            return new TextNode(expression.getExpressionString());
        }
    }

    @Override
    public <T> T evaluate(Object root, Class<T> targetType) {
        EvaluationContext context = contextFactory.create(root);
        try {
            return expression.getValue(context, targetType);
        } catch (Exception e) {
            return null;
        }
    }
}
