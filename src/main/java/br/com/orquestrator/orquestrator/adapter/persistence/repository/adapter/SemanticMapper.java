package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.SemanticDefinitionEntity;
import br.com.orquestrator.orquestrator.domain.rules.SemanticDefinition;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * SemanticMapper: Tradutor de Entidades de Semântica para Domínio.
 */
@Component
public class SemanticMapper {

    public SemanticDefinition toDomain(SemanticDefinitionEntity entity) {
        if (entity == null) return null;

        return new SemanticDefinition(
            entity.getTypeName(),
            entity.getDescription(),
            entity.getFormatScript(),
            entity.getValidationScript()
        );
    }

    public List<SemanticDefinition> toDomainList(List<SemanticDefinitionEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
            .map(this::toDomain)
            .toList();
    }
}
