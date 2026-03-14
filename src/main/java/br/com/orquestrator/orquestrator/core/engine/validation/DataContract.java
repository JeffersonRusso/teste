package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;

/**
 * DataContract: Contrato de validação para um sinal de dados.
 */
public interface DataContract {
    
    /**
     * Valida um DataNode contra as regras do contrato.
     * @throws RuntimeException se a validação falhar.
     */
    void validate(DataNode value);
}
