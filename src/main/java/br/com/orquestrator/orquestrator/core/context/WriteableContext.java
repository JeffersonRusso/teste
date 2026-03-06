package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;

public interface WriteableContext {
    /** Grava um valor usando uma String de caminho. */
    void put(String key, DataValue value);

    /** Grava um valor usando um DataPath tipado. */
    default void put(DataPath path, DataValue value) {
        put(path.value(), value);
    }
    
    void addTag(String tag);
}
