package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeDecoratorRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineVersionRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeDecoratorEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineVersionEntity;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineRepository;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaPipelineRepositoryAdapter implements PipelineRepository {

    private final PipelineVersionRepository versionRepository;
    private final PipelineNodeRepository nodeRepository;
    private final PipelineNodeDecoratorRepository decoratorRepository;

    @Override
    @Cacheable(value = "pipeline_definitions", key = "#operationType")
    public Optional<PipelineDefinition> findActive(String operationType) {
        return versionRepository.findActive(operationType)
                .map(this::mapToDefinition);
    }

    private PipelineDefinition mapToDefinition(PipelineVersionEntity version) {
        var nodes = nodeRepository.findByPipelineId(version.getPipelineId()).stream()
                .map(this::mapToTaskDefinition)
                .toList();

        return new PipelineDefinition(
                version.getOperationType(),
                version.getVersion(),
                version.getTimeoutMs(),
                version.getInputMapping(),
                version.getRequiredOutputs(),
                nodes
        );
    }

    private TaskDefinition mapToTaskDefinition(PipelineNodeEntity entity) {
        String finalType = entity.getType() != null ? entity.getType() : 
                          (entity.getTemplate() != null ? entity.getTemplate().getType() : "UNKNOWN");

        Map<String, Object> finalConfig = new HashMap<>();
        if (entity.getTemplate() != null && entity.getTemplate().getBaseConfiguration() != null) {
            finalConfig.putAll(entity.getTemplate().getBaseConfiguration());
        }
        if (entity.getConfiguration() != null) {
            finalConfig.putAll(entity.getConfiguration() .toFullMap()); // CORREÇÃO AQUI
        }

        var features = decoratorRepository.findByNodeIdOrderByExecutionOrderAsc(entity.getNodeId()).stream()
                .map(this::resolveDecorator)
                .toList();

        return new TaskDefinition(
                new NodeId(entity.getName()), 
                1, 
                entity.getName(),
                finalType,
                extractTimeout(finalConfig), 
                finalConfig,
                features,
                entity.getFailFast(),
                entity.getInputs(),
                entity.getOutputs(),
                entity.getActivationTags(),
                entity.getGuardCondition(),
                false, 
                0 
        );
    }

    private long extractTimeout(Map<String, Object> config) {
        Object timeout = config.get("timeoutMs");
        if (timeout instanceof Number n) return n.longValue();
        return 0L;
    }

    private FeatureDefinition resolveDecorator(PipelineNodeDecoratorEntity entity) {
        Map<String, Object> finalConfig = new HashMap<>(entity.getTemplate().getDefaultConfiguration());
        if (entity.getOverrideConfiguration() != null) {
            finalConfig.putAll(entity.getOverrideConfiguration());
        }
        return new FeatureDefinition(
                entity.getTemplate().getType(),
                entity.getTemplateId(),
                finalConfig
        );
    }
}
