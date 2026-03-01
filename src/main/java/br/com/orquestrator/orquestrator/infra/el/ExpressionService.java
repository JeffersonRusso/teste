package br.com.orquestrator.orquestrator.infra.el;

import java.util.Map;

/**
 * ExpressionService: Serviço puro de avaliação de expressões.
 * Esconde a complexidade de criação de contextos e foca na entrega de resultados.
 */
public interface ExpressionService {

    /**
     * Avalia uma expressão contra o contexto soberano (global).
     */
    <T> T evaluate(String expression, Class<T> type);

    /**
     * Avalia uma expressão contra um objeto raiz específico.
     * Útil para navegar em resultados de tasks sem poluir o contexto global.
     */
    <T> T evaluate(Object root, String expression, Class<T> type);

    /**
     * Resolve um template (interpolação) contra o contexto soberano.
     */
    <T> T resolve(String template, Class<T> type);

    /**
     * Resolve recursivamente todas as expressões em um mapa contra o contexto soberano.
     */
    Map<String, Object> resolveMap(Map<String, Object> source);
}
