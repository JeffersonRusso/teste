package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpression;

/**
 * SpelCompiledExpression: Bolinha de lógica dinâmica baseada em SpEL.
 */
@RequiredArgsConstructor
public final class SpelCompiledExpression implements CompiledExpression {

    private final Expression expression;
    private final SpelContextFactory contextFactory;

    @Override
    public DataValue evaluate(Object root) {
        EvaluationContext context = ContextHolder.EVAL_CONTEXT.isBound() 
            ? ContextHolder.EVAL_CONTEXT.get() 
            : contextFactory.create(root);
        try {
            return DataValue.of(expression.getValue(context));
        } catch (Exception e) {
            return DataValue.of(expression.getExpressionString());
        }
    }

    @Override
    public <T> T evaluate(Object root, Class<T> targetType) {
        EvaluationContext context = ContextHolder.EVAL_CONTEXT.isBound() 
            ? ContextHolder.EVAL_CONTEXT.get() 
            : contextFactory.create(root);
        try {
            return expression.getValue(context, targetType);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String rootField() {
        if (expression instanceof SpelExpression spel) {
            var node = spel.getAST();
            while (node != null) {
                if (node instanceof PropertyOrFieldReference prop) return prop.getName();
                if (node.getChildCount() > 0) node = node.getChild(0);
                else break;
            }
        }
        return "";
    }

    @Override
    public void setValue(Object root, Object value) {
        EvaluationContext context = ContextHolder.EVAL_CONTEXT.isBound() 
            ? ContextHolder.EVAL_CONTEXT.get() 
            : contextFactory.create(root);
        try {
            expression.setValue(context, value);
        } catch (Exception ignored) {}
    }
}
