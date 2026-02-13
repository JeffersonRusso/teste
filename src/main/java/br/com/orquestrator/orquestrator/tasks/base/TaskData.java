package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.vo.DataValue;
import java.util.Map;

/**
 * Interface semântica para acesso a dados da task.
 * Abstrai a complexidade de tipos e origens de dados.
 */
public interface TaskData {

    /** Obtém um dado de entrada encapsulado em um DataValue */
    DataValue get(String key);

    /** Registra um dado de saída */
    Object put(String key, Object value);

    /** Verifica existência de um dado */
    boolean has(String key);

    /** Adiciona metadados de execução */
    void addMetadata(String key, Object value);

    /** Obtém um metadado */
    Object getMetadata(String key);

    /**
     * Retorna uma visão desta interface como um Map<String, Object>
     * contendo os valores brutos (unwrapped) para integração com engines.
     */
    Map<String, Object> asMap();
}
