package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TagRuleEntity;
import br.com.orquestrator.orquestrator.domain.rules.TagRule;

import java.util.Collections;
import java.util.List;

/**
 * TagRuleMapper: Tradutor puro de Entidades de Tag para Domínio.
 */
public final class TagRuleMapper {

    private TagRuleMapper() {}

    public static TagRule toDomain(TagRuleEntity entity) {
        if (entity == null) return null;

        return new TagRule(
            entity.getTagName(),
            entity.getConditionExpression(),
            entity.getPriority() != null ? entity.getPriority() : 0,
            Boolean.TRUE.equals(entity.getIsActive())
        );
    }

    public static List<TagRule> toDomainList(List<TagRuleEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
            .map(TagRuleMapper::toDomain)
            .toList();
    }
}
