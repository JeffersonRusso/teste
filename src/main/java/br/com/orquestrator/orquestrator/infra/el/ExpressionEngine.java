package br.com.orquestrator.orquestrator.infra.el;

/**
 * ExpressionEngine: O contrato para o motor de lógica dinâmica.
 */
public interface ExpressionEngine {
    
    /** Compila um objeto em uma expressão executável. */
    CompiledExpression compile(Object expression);

}
