package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InputNormalizationRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineVersionRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineVersionEntity;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineRepository;
import br.com.orquestrator.orquestrator.core.pipeline.TaskRepository;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaPipelineRepositoryAdapter implements PipelineRepository, TaskRepository {

    private final PipelineVersionRepository versionRepository;
    private final PipelineNodeRepository nodeRepository;
    private final InputNormalizationRepository normalizationRepository;

    @Override
    @Cacheable(value = "pipeline_definitions", key = "#operationType")
    public Optional<PipelineDefinition> findActive(String operationType) {
        return versionRepository.findActive(operationType)
                .map(this::mapToDefinition);
    }

    @Override
    @Cacheable(value = "task_definitions", key = "#name")
    public Optional<TaskDefinition> findByName(String name) {
        return nodeRepository.findByName(name)
                .map(PipelineNodeEntity::toDomain);
    }

    private PipelineDefinition mapToDefinition(PipelineVersionEntity version) {
        var nodes = nodeRepository.findByPipelineId(version.getPipelineId()).stream()
                .map(PipelineNodeEntity::toDomain)
                .toList();

        // Carrega normalização da tabela tb_input_normalization
        Map<String, String> inputMapping = normalizationRepository.findByOperationType(version.getOperationType()).stream()
                .collect(Collectors.toMap(InputNormalizationEntity::getTargetField, InputNormalizationEntity::getSourceExpression));

        return new PipelineDefinition(
                version.getOperationType(),
                version.getVersion(),
                version.getTimeoutMs(),
                inputMapping,
                version.getRequiredOutputs(),
                nodes
        );
    }
}
