package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.expression.Expression;
import java.util.Map;

/**
 * ExpressionEngine: O Oráculo para consultas e transformações dinâmicas.
 */
public interface ExpressionEngine {
    <T> T evaluate(String expression, Object root, Class<T> targetType);
    <T> T resolve(String template, Object root, Class<T> targetType);
    Map<String, Object> resolveMap(Map<String, Object> source, Object root);
    void setValue(Object root, String path, Object value);

    // NOVO: Suporte a expressões pré-compiladas
    Expression parse(String expression);
    <T> T execute(Expression compiledExpression, Object root, Class<T> targetType);
}
