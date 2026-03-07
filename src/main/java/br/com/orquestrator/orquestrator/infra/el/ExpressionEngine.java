package br.com.orquestrator.orquestrator.infra.el;

/**
 * ExpressionEngine: Fábrica de bolinhas de lógica.
 */
public interface ExpressionEngine {
    
    /** 
     * Compila um valor em uma expressão executável.
     * Se for String, parseia como SpEL. Se for outro objeto, vira uma constante.
     */
    CompiledExpression compile(Object value);
}
