package br.com.orquestrator.orquestrator.core.context.normalization;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;

import java.util.List;

public interface NormalizationRuleProvider {
    List<InputNormalizationEntity> getRules(String operationType);
}
