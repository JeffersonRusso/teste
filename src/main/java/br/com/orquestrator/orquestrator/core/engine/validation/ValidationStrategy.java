package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.DataValue;

/**
 * ValidationStrategy: Define como validar um tipo específico de dado.
 */
public interface ValidationStrategy {
    /** Retorna o tipo de dado que esta estratégia atende. */
    DataType getType();

    /** Executa a validação contra o contrato compilado. */
    void validate(ContractRegistry.CompiledContract compiled, DataValue value);
}
