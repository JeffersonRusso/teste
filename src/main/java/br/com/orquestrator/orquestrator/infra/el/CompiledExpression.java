package br.com.orquestrator.orquestrator.infra.el;

/**
 * CompiledExpression: Representa uma expressão pré-compilada agnóstica.
 */
public interface CompiledExpression {
    
    /** 
     * Avalia a expressão contra um objeto raiz.
     * Implementação padrão delega para a versão genérica.
     */
    default Object evaluate(Object root) {
        return evaluate(root, Object.class);
    }

    /** 
     * Avalia a expressão retornando um tipo específico.
     */
    <T> T evaluate(Object root, Class<T> targetType);
}
