package br.com.orquestrator.orquestrator.domain.model.data;

import java.util.Optional;

/**
 * DataNode: Abstração agnóstica para um nó de dados estruturados.
 */
public interface DataNode {

    /**
     * Busca um valor de forma inteligente.
     * Se começar com '/', trata como JSON Pointer (at).
     * Caso contrário, trata como chave de objeto (get).
     */
    DataNode find(String path);

    /** Navegação rigorosa via JSON Pointer (RFC 6901). */
    DataNode at(String path);

    /** Acesso direto a campo ou chave de primeiro nível. */
    DataNode get(String field);

    /** Acesso a índice de array. */
    DataNode get(int index);

    // ===================================================================
    // Verificadores
    // ===================================================================
    boolean isMissing();
    boolean isObject();
    boolean isArray();
    boolean isValue();

    // ===================================================================
    // Extração
    // ===================================================================
    Optional<String> asText();
    Optional<Integer> asInt();
    Optional<Double> asDouble();
    Optional<Boolean> asBoolean();
    Object asNative();
}
