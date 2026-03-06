package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import java.util.Map;

/**
 * ReadableContext: Visão restrita para leitura de dados.
 * Retorna sempre DataValue para preservar a semântica e metadados.
 */
public interface ReadableContext {
    
    /** Retorna o DataValue completo (Estrutura + Semântica). */
    DataValue get(String key);

    /** Retorna o DataValue completo usando um DataPath tipado. */
    default DataValue get(DataPath path) {
        return get(path.value());
    }

    /** Atalho para obter o valor bruto (raw). */
    default Object getRaw(String key) {
        return get(key).raw();
    }

    /** Retorna o estado completo do contexto. */
    Map<String, Object> getRoot();
    
    boolean contains(String key);
}
