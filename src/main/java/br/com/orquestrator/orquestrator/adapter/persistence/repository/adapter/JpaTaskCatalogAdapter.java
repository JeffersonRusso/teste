package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InfraProfileRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InfraProfileEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogEntity;
import br.com.orquestrator.orquestrator.domain.factory.TaskDefinitionFactory;
import br.com.orquestrator.orquestrator.domain.factory.TaskRawData;
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
        Map<String, JsonNode> profileControls = loadProfileControls();

        return taskRepository.findAllActive().stream()
                .map(entity -> mapToDomain(entity, profileControls))
                .toList();
    }

    private Map<String, JsonNode> loadProfileControls() {
        return profileRepository.findAll().stream()
                .collect(Collectors.toMap(
                        InfraProfileEntity::getProfileId,
                        InfraProfileEntity::getDefaultControls,
                        (existing, _) -> existing
                ));
    }

    private TaskDefinition mapToDomain(TaskCatalogEntity entity, Map<String, JsonNode> profileControls) {
        JsonNode defaultControls = entity.getInfraProfileId() != null 
                ? profileControls.get(entity.getInfraProfileId()) 
                : null;

        // Uso do novo padrão TaskRawData (SOLID)
        TaskRawData raw = new TaskRawData(
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

        return taskFactory.create(raw);
    }
}
