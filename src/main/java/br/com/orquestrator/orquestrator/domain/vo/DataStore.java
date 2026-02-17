package br.com.orquestrator.orquestrator.domain.vo;

import java.util.Map;
import java.util.Optional;

/**
 * Abstração para armazenamento e recuperação de dados via caminhos (dot notation).
 */
public interface DataStore {
    void put(String path, Object value);
    Object get(String path);
    <T> Optional<T> get(String path, Class<T> clazz);
    Map<String, Object> getRoot();
}
