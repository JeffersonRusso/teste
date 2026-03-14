package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.core.engine.validation.DataContract;

/**
 * DataContractFactory: Porta de saída para criação de implementações de contrato.
 */
public interface DataContractFactory {
    
    /**
     * Cria um contrato de validação baseado na definição técnica (ex: JSON Schema).
     */
    DataContract create(String key, String schemaDefinition);
}
