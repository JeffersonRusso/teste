package br.com.orquestrator.orquestrator.core.context.storage;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * DataStore: Contrato único para armazenamento tipado.
 * Alinhado com ReadableContext e WriteableContext.
 */
public interface DataStore {
    /** Grava um DataValue. */
    void put(String key, DataValue value);

    /** Retorna o DataValue completo. */
    DataValue get(String key);

    /** Retorna o estado completo do armazenamento. */
    Map<String, Object> getAll();

    boolean contains(String key);
}
