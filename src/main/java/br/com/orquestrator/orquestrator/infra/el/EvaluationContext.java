package br.com.orquestrator.orquestrator.infra.el;

import java.util.Objects;

/**
 * Encapsula o estado de uma avaliação e expõe métodos fluentes para resolução.
 * Atua como uma ponte entre o domínio e o motor de expressões.
 */
public record EvaluationContext(Object nativeContext, ExpressionEngine engine) {

    public EvaluationContext {
        Objects.requireNonNull(nativeContext, "O contexto nativo não pode ser nulo");
        Objects.requireNonNull(engine, "O motor de expressões não pode ser nulo");
    }

    /**
     * Avalia uma expressão pura.
     */
    public <T> T evaluate(String expression, Class<T> type) {
        return engine.evaluate(expression, nativeContext, type);
    }

    /**
     * Resolve um valor que pode ser literal, template #{...} ou expressão #...
     */
    public <T> T resolve(String value, Class<T> type) {
        if (value == null) return null;
        
        // Só avalia se for explicitamente uma expressão (#...) ou template (#{...})
        if (isExplicitExpression(value)) {
            return evaluate(value, type);
        }
        
        // Se for um literal e o tipo esperado for String, retorna direto
        if (type == String.class) {
            return type.cast(value);
        }
        
        // Para outros tipos, retorna o valor original (o Jackson cuidará da conversão na árvore)
        return (T) value;
    }

    private boolean isExplicitExpression(String value) {
        // Uma expressão SpEL válida para nós DEVE começar com # ou conter o delimitador de template #{
        return value.startsWith("#") || value.contains("#{");
    }
}
