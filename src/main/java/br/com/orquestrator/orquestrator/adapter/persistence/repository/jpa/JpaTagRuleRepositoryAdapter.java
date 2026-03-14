package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter.TagRuleMapper;
import br.com.orquestrator.orquestrator.core.ports.output.TagRuleProvider;
import br.com.orquestrator.orquestrator.domain.rules.TagRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JpaTagRuleRepositoryAdapter: Única porta de entrada (Pública) para o JPA de Tags.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaTagRuleRepositoryAdapter implements TagRuleProvider {

    private final TagRuleRepository repository;

    @Override
    @Cacheable(value = "active_tag_rules")
    public List<TagRule> findAllActive() {
        log.info("Buscando regras de tag ativas do banco de dados");
        
        return TagRuleMapper.toDomainList(
            repository.findAllActive()
        );
    }
}
