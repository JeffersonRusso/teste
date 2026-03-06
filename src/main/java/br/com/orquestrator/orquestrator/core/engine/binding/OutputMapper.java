package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.domain.model.DataValue;

/**
 * OutputMapper: Estratégia de extração de dados de um resultado.
 */
public interface OutputMapper {
    DataValue map(Object result);
}
