package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import java.util.Map;

/**
 * DataFactory: Porta de saída para criação de estruturas de dados agnósticas.
 */
public interface DataFactory {
    
    /** Cria um DataNode a partir de um mapa nativo. */
    DataNode createObject(Map<String, Object> map);
    
    /** Cria um DataNode a partir de um valor simples (String, Int, Boolean). */
    DataNode createValue(Object value);
    
    /** Cria um DataNode "missing" (vazio/nulo). */
    DataNode missing();
    
    /** (Opcional) Faz o parsing de uma string JSON/XML para DataNode. */
    DataNode parse(String rawData);
}
