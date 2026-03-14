package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.DataContractEntity;
import br.com.orquestrator.orquestrator.domain.rules.DataContract;

/**
 * DataContractMapper: Tradutor puro de Entidades de Contrato para Domínio.
 */
public final class DataContractMapper {

    private DataContractMapper() {}

    public static DataContract toDomain(DataContractEntity entity) {
        if (entity == null) return null;

        return new DataContract(
            entity.getContextKey(),
            entity.getSchemaDefinition(),
            entity.getDescription()
        );
    }
}
