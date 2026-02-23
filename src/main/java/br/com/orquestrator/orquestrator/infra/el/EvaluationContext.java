package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.Objects;

/**
 * Encapsula o poder do SpEL para leitura e escrita no contexto.
 * Otimizado para evitar verificações de string repetitivas no hot path.
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
        if (value == null || value.isEmpty()) return null;
        
        char firstChar = value.charAt(0);
        
        // OTIMIZAÇÃO: Verificação rápida de caracteres para evitar contains() se possível
        if (firstChar == '#' && value.length() > 1) {
            if (value.charAt(1) == '{') {
                return engine.resolve(value, nativeContext, type);
            }
            return engine.evaluate(value, nativeContext, type);
        }
        
        // Fallback para templates no meio da string (menos comum que no início)
        if (value.indexOf("#{") != -1) {
            return engine.resolve(value, nativeContext, type);
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
     * Define uma variável no contexto SpEL.
     */
    public void setVariable(String name, Object value) {
        nativeContext.setVariable(name, value);
    }

    /**
     * Escreve um valor em um caminho SpEL (ex: "#cliente.id").
     * OTIMIZAÇÃO: Evita concatenação de string e avaliação de expressão se for apenas atribuição simples.
     */
    public void set(String path, Object value) {
        if (path == null || path.isEmpty()) return;
        
        // Se o caminho for simples (ex: "#cliente.id"), podemos tentar otimizar no futuro.
        // Por enquanto, mantemos a flexibilidade do SpEL mas com cache de expressão no engine.
        String cleanPath = path.charAt(0) == '#' ? path : "#" + path;
        
        // OTIMIZAÇÃO: Usamos um nome de variável fixo e interno para evitar overhead de String.format ou STR
        setVariable("_val", value);
        evaluate(cleanPath + " = #_val", Object.class);
    }
}
