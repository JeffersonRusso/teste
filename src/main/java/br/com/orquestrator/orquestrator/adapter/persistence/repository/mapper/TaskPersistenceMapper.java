package br.com.orquestrator.orquestrator.adapter.persistence.repository.mapper;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogEntity;
import br.com.orquestrator.orquestrator.domain.factory.TaskRawData;
import br.com.orquestrator.orquestrator.domain.model.DataMapping;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.infra.repository.entity.json.DataMappingEntityRecord;
import br.com.orquestrator.orquestrator.infra.repository.entity.json.FeaturePhasesEntityRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TaskPersistenceMapper {

    private final ObjectMapper objectMapper;

    public TaskRawData toDomain(TaskCatalogEntity entity, List<FeatureDefinition> profileFeatures) {
        List<FeatureDefinition> allFeatures = new ArrayList<>();
        
        // 1. Adiciona features do perfil (já resolvidas pelo adaptador)
        if (profileFeatures != null) {
            allFeatures.addAll(profileFeatures);
        }
        
        // 2. Adiciona features da task (específicas)
        if (entity.getFeatures() != null) {
            allFeatures.addAll(mapFeatures(entity.getFeatures()));
        }

        return new TaskRawData(
                entity.getTaskId(),
                entity.getTaskVersion(),
                entity.getTaskType(),
                convertToMap(entity.getConfig()),
                allFeatures,
                entity.getSelectorExpression(),
                entity.getCriticality(),
                mapMappings(entity.getRequires()),
                mapMappings(entity.getProduces()),
                convertToMap(entity.getResponseSchema())
        );
    }

    private List<FeatureDefinition> mapFeatures(FeaturePhasesEntityRecord record) {
        List<FeatureDefinition> list = new ArrayList<>();
        if (record.monitors() != null) list.addAll(mapDefs(record.monitors()));
        if (record.preExecution() != null) list.addAll(mapDefs(record.preExecution()));
        if (record.postExecution() != null) list.addAll(mapDefs(record.postExecution()));
        return list;
    }

    private List<FeatureDefinition> mapDefs(List<FeaturePhasesEntityRecord.FeatureDefinitionEntityRecord> records) {
        if (records == null) return List.of();
        return records.stream()
                .map(r -> new FeatureDefinition(r.type(), r.templateRef(), r.config()))
                .toList();
    }

    private List<DataMapping> mapMappings(List<DataMappingEntityRecord> records) {
        if (records == null) return List.of();
        return records.stream().map(r -> new DataMapping(r.name(), r.path(), r.type())).toList();
    }

    private Map<String, Object> convertToMap(JsonNode node) {
        if (node == null || node.isMissingNode()) return Map.of();
        return objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
    }
}
