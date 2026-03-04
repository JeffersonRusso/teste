package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.Objects;

/**
 * EvaluationContext: Wrapper puro para avaliação de expressões.
 * Focado exclusivamente em LEITURA e TRANSFORMAÇÃO (CQS).
 */
public record EvaluationContext(StandardEvaluationContext nativeContext, ExpressionEngine engine) {

    public EvaluationContext {
        Objects.requireNonNull(nativeContext, "O contexto nativo não pode ser nulo");
        Objects.requireNonNull(engine, "O motor de expressão não pode ser nulo");
    }

    /**
     * Avalia uma expressão ou resolve um template.
     */
    public <T> T resolve(String expression, Class<T> type) {
        if (expression == null || expression.isBlank()) return null;

        if (expression.contains("${")) {
            return engine.resolve(expression, nativeContext, type);
        }

        return engine.evaluate(expression, nativeContext, type);
    }

    /**
     * Avalia uma expressão SpEL pura.
     */
    public <T> T evaluate(String expression, Class<T> type) {
        return engine.evaluate(expression, nativeContext, type);
    }

    /**
     * Define uma variável local para a sessão de avaliação (ex: #result).
     */
    public void setVariable(String name, Object value) {
        nativeContext.setVariable(name, value);
    }
}
