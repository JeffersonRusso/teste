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

    public <T> T evaluate(String expression, Class<T> type) {
        return engine.evaluate(expression, nativeContext, type);
    }

    /**
     * Escreve um valor em um caminho SpEL (ex: "cliente.id").
     * O SpEL cuida da criação da hierarquia se configurado corretamente.
     */
    public void set(String path, Object value) {
        // Remove o '#' se presente para o comando de atribuição
        String cleanPath = path.startsWith("#") ? path.substring(1) : path;
        evaluate(STR."#\{cleanPath} = #value", Object.class); 
        // Nota: O 'value' deve ser passado como variável no nativeContext
    }

    public <T> T resolve(String value, Class<T> type) {
        if (value == null) return null;
        if (isExplicitExpression(value)) return evaluate(value, type);
        return (type == String.class) ? type.cast(value) : (T) value;
    }

    private boolean isExplicitExpression(String value) {
        return value.startsWith("#") || value.contains("#{");
    }
}
