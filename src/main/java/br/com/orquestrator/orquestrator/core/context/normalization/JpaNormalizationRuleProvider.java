package br.com.orquestrator.orquestrator.core.context.normalization;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InputNormalizationRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaNormalizationRuleProvider implements NormalizationRuleProvider {

    private final InputNormalizationRepository repository;

    @Override
    @Cacheable(value = "normalization_rules", key = "#operationType", unless = "#result.isEmpty()")
    public List<InputNormalizationEntity> getRules(String operationType) {
        return repository.findByOperationType(operationType);
    }
}
