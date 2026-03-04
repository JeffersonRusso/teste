package br.com.orquestrator.orquestrator.infra.el;

import java.util.Map;

/**
 * ExpressionEngine: O Oráculo Único para dados dinâmicos.
 */
public interface ExpressionEngine {
    
    /** Avalia lógica pura (ex: #raw.amount > 100) */
    <T> T evaluate(String expression, Object root, Class<T> targetType);
    
    /** Resolve templates (ex: Hello ${standard.name}) */
    <T> T resolve(String template, Object root, Class<T> targetType);
    
    /** Grava dados em caminhos profundos (Auto-Grow) */
    void setValue(Object root, String path, Object value);

    /** Resolve um mapa recursivamente (útil para bindings de configuração) */
    Map<String, Object> resolveMap(Map<String, Object> source, Object root);
}
