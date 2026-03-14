package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter.SemanticMapper;
import br.com.orquestrator.orquestrator.core.ports.output.SemanticProvider;
import br.com.orquestrator.orquestrator.domain.rules.SemanticDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * JpaSemanticProviderAdapter: Implementação da porta SemanticProvider.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSemanticProviderAdapter implements SemanticProvider {

    private final SemanticDefinitionRepository repository;
    private final SemanticMapper mapper;

    @Override
    @Cacheable(value = "semantic_definitions")
    public List<SemanticDefinition> findAll() {
        log.info("Buscando definições semânticas do banco...");
        return mapper.toDomainList(repository.findAll());
    }
}
