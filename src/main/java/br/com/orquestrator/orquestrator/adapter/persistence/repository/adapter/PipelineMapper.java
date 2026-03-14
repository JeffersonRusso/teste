package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeInputEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeOutputEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineVersionEntity;
import br.com.orquestrator.orquestrator.domain.model.*;
import br.com.orquestrator.orquestrator.domain.model.definition.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.model.vo.NodeId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * PipelineMapper: Centraliza a tradução entre as Entidades Relacionais e o Domínio Rico.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineMapper {

    private final ObjectMapper mapper;
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    public PipelineDefinition toDefinition(PipelineVersionEntity version, List<TaskDefinition> nodes) {
        if (version == null) return null;

        return new PipelineDefinition(
                version.getOperationType(),
                version.getVersion(),
                version.getTimeoutMs() != null ? version.getTimeoutMs() : 30000L,
                version.getRequiredOutputs() != null ? version.getRequiredOutputs() : Collections.emptySet(),
                nodes != null ? nodes : Collections.emptyList(),
                version.getExecutionStrategy()
        );
    }

    public TaskDefinition toDomain(PipelineNodeEntity entity) {
        if (entity == null) return null;

        Map<String, Object> rawConfig = convertJsonToMap(entity.getConfiguration());

        return new TaskDefinition(
            new NodeId(entity.getNodeId().toString()),
            entity.getType(),
            extractBehavior(rawConfig),
            extractFeatures(rawConfig),
            extractTaskConfig(rawConfig),
            mapInputs(entity.getInputs()),
            mapOutputs(entity.getOutputs())
        );
    }

    private TaskBehavior extractBehavior(Map<String, Object> root) {
        Map<String, Object> b = (Map<String, Object>) root.getOrDefault("behavior", Collections.emptyMap());
        return new TaskBehavior(
            (Boolean) b.getOrDefault("failFast", true),
            b.containsKey("tags") ? new HashSet<>((List<String>) b.get("tags")) : Set.of("default"),
            (Boolean) b.getOrDefault("global", false),
            (String) b.get("cron")
        );
    }

    private List<FeatureDefinition> extractFeatures(Map<String, Object> root) {
        List<Map<String, Object>> f = (List<Map<String, Object>>) root.getOrDefault("features", Collections.emptyList());
        return f.stream()
                .map(m -> new FeatureDefinition((String) m.get("type"), (Map<String, Object>) m.get("config")))
                .toList();
    }

    private Map<String, Object> extractTaskConfig(Map<String, Object> root) {
        return (Map<String, Object>) root.getOrDefault("config", Collections.emptyMap());
    }

    private Map<String, Object> convertJsonToMap(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isMissingNode() || jsonNode.isNull()) {
            return Collections.emptyMap();
        }
        try {
            JsonNode actualNode = jsonNode.isTextual() ? mapper.readTree(jsonNode.asText()) : jsonNode;
            return mapper.convertValue(actualNode, MAP_TYPE);
        } catch (Exception e) {
            log.error("Erro ao processar JSON: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private List<TaskInput> mapInputs(List<PipelineNodeInputEntity> inputs) {
        if (inputs == null) return Collections.emptyList();
        return inputs.stream()
            .map(i -> new TaskInput(i.getLocalKey(), i.getSourceSignal(), i.getSourcePath(), 
                                   Boolean.TRUE.equals(i.getIsRequired()), i.getExpectedSemanticType()))
            .toList();
    }

    private List<TaskOutput> mapOutputs(List<PipelineNodeOutputEntity> outputs) {
        if (outputs == null) return Collections.emptyList();
        return outputs.stream()
            .map(o -> new TaskOutput(o.getLocalKey(), o.getTargetSignal(), o.getProducedSemanticType()))
            .toList();
    }
}
