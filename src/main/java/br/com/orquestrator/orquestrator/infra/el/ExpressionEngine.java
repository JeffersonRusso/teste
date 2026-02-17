package br.com.orquestrator.orquestrator.infra.el;

/**
 * Motor de Expressão: Define a intenção da avaliação.
 */
public interface ExpressionEngine {
    /** Avalia lógica pura (SpEL nativo). */
    <T> T evaluate(String expression, Object nativeContext, Class<T> targetType);
    
    /** Resolve um template de string (Interpolação com #{}). */
    <T> T resolve(String template, Object nativeContext, Class<T> targetType);
}
