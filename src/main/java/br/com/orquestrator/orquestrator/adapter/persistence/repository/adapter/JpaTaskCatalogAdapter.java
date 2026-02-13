package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InfraProfileRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InfraProfileEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogEntity;
import br.com.orquestrator.orquestrator.domain.factory.TaskDefinitionFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador JPA para o catálogo de tasks.
 * Gerencia a recuperação de tasks ativas e a resolução de seus perfis de infraestrutura.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JpaTaskCatalogAdapter implements TaskCatalogProvider {

    private final TaskCatalogRepository taskRepository;
    private final InfraProfileRepository profileRepository;
    private final TaskDefinitionFactory taskFactory;

    @Override
    @Cacheable(value = "task_definitions", unless = "#result.isEmpty()")
    public List<TaskDefinition> findAllActive() {
        log.debug("Cache MISS: Recarregando catálogo de tasks e perfis do banco de dados.");

        // 1. Carrega todos os perfis para resolução em memória (Otimização N+1)
        Map<String, JsonNode> profileControls = loadProfileControls();

        // 2. Busca tasks e realiza o mapeamento para o domínio
        return taskRepository.findAllActive().stream()
                .map(entity -> mapToDomain(entity, profileControls))
                .toList();
    }

    private Map<String, JsonNode> loadProfileControls() {
        return profileRepository.findAll().stream()
                .collect(Collectors.toMap(
                        InfraProfileEntity::getProfileId,
                        InfraProfileEntity::getDefaultControls,
                        (existing, replacement) -> replacement
                ));
    }

    private TaskDefinition mapToDomain(TaskCatalogEntity entity, Map<String, JsonNode> profileControls) {
        // Resolve os controles padrão do perfil, se houver um associado à task
        JsonNode defaultControls = entity.getInfraProfileId() != null 
                ? profileControls.get(entity.getInfraProfileId()) 
                : null;

        return taskFactory.create(
                entity.getTaskId(),
                entity.getVersion(),
                entity.getTaskType(),
                entity.getConfig(),
                entity.getFeatures(),
                defaultControls,
                entity.getSelectorExpression(),
                entity.getCriticality(),
                entity.getRequires(),
                entity.getProduces(),
                entity.getResponseSchema()
        );
    }
}
