package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InfraProfileRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InfraProfileEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.ProfileFeatureEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.mapper.TaskPersistenceMapper;
import br.com.orquestrator.orquestrator.domain.factory.TaskDefinitionFactory;
import br.com.orquestrator.orquestrator.domain.factory.TaskRawData;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaTaskCatalogAdapter implements TaskCatalogProvider {

    private final TaskCatalogRepository taskRepository;
    private final InfraProfileRepository profileRepository;
    private final TaskDefinitionFactory taskFactory;
    private final TaskPersistenceMapper mapper;

    @Override
    @Cacheable(value = "task_definitions", unless = "#result.isEmpty()")
    public List<TaskDefinition> findAllActive() {
        var infrastructureTemplates = fetchInfrastructureTemplates();

        return taskRepository.findAllActive().stream()
                .map(entity -> assembleTask(entity, infrastructureTemplates))
                .toList();
    }

    private Map<String, List<FeatureDefinition>> fetchInfrastructureTemplates() {
        return profileRepository.findAll().stream()
                .collect(Collectors.toMap(
                        InfraProfileEntity::getProfileId,
                        this::mapToFeatureDefinitions
                ));
    }

    private List<FeatureDefinition> mapToFeatureDefinitions(InfraProfileEntity profile) {
        List<FeatureDefinition> definitions = new ArrayList<>();
        if (profile.getFeatures() != null) {
            for (ProfileFeatureEntity pf : profile.getFeatures()) {
                // AQUI: Carregamos a configuração real do template
                definitions.add(new FeatureDefinition(
                        pf.getTemplate().getFeatureType(),
                        pf.getTemplate().getTemplateId(),
                        pf.getTemplate().getConfig() 
                ));
            }
        }
        return definitions;
    }

    private TaskDefinition assembleTask(TaskCatalogEntity entity, Map<String, List<FeatureDefinition>> templates) {
        List<FeatureDefinition> profileFeatures = entity.getInfraProfileId() != null 
                ? templates.get(entity.getInfraProfileId()) 
                : List.of();

        TaskRawData raw = mapper.toDomain(entity, profileFeatures);
        return taskFactory.create(raw);
    }
}
