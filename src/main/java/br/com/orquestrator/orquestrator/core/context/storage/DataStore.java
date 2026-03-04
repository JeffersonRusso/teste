package br.com.orquestrator.orquestrator.core.context.storage;

import java.util.Map;

/**
 * DataStore: Contrato único para armazenamento.
 */
public interface DataStore {
    /**
     * Grava um dado. Se a chave contiver pontos (ex: a.b.c), 
     * cria a estrutura aninhada automaticamente.
     */
    void put(String key, Object value);

    Object get(String key);
    Map<String, Object> getAll();
    boolean contains(String key);
}
