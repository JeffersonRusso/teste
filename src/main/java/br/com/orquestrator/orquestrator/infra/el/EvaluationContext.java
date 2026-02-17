package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.Objects;

/**
 * Encapsula o poder do SpEL para leitura e escrita no contexto.
 */
public record EvaluationContext(StandardEvaluationContext nativeContext, ExpressionEngine engine) {

    public EvaluationContext {
        Objects.requireNonNull(nativeContext, "O contexto nativo não pode ser nulo");
    }

    /**
     * Resolve um valor de forma inteligente.
     * Se contiver #{...}, trata como template.
     * Se começar com #, trata como lógica pura.
     * Caso contrário, retorna o valor literal.
     */
    public <T> T resolve(String value, Class<T> type) {
        if (value == null) return null;
        
        if (value.contains("#{")) {
            return engine.resolve(value, nativeContext, type);
        }
        
        if (value.startsWith("#")) {
            return engine.evaluate(value, nativeContext, type);
        }

        return (type == String.class) ? type.cast(value) : (T) value;
    }

    /**
     * Atalho para avaliar lógica pura (SpEL nativo).
     */
    public <T> T evaluate(String expression, Class<T> type) {
        return engine.evaluate(expression, nativeContext, type);
    }

    /**
     * Escreve um valor em um caminho SpEL (ex: "#cliente.id").
     */
    public void set(String path, Object value) {
        String cleanPath = path.startsWith("#") ? path : "#" + path;
        nativeContext.setVariable("value", value);
        evaluate(STR."\{cleanPath} = #value", Object.class);
    }
}
