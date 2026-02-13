package br.com.orquestrator.orquestrator.infra.el;

/**
 * Contrato de baixo nível para execução de expressões.
 * Focado puramente na execução técnica contra um contexto nativo.
 */
public interface ExpressionEngine {
    <T> T evaluate(String expression, Object nativeContext, Class<T> targetType);
}
