package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.DataMapping;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.infra.config.ConfigVariableResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDefinitionFactory {

    private final ObjectMapper objectMapper;
    private final ConfigVariableResolver variableResolver;

    private static final long DEFAULT_TIMEOUT = 2000L;

    public TaskDefinition create(TaskRawData raw) {
        // 1. Resolve variáveis de ambiente no Map de configuração
        Map<String, Object> resolvedConfig = variableResolver.resolve(raw.config());
        
        // 2. Extrai configuração técnica básica usando o ObjectMapper para converter o Map
        TaskConfigRecord config = parseConfig(resolvedConfig, raw.taskId());
        
        // 3. Montagem final
        return buildDefinition(raw, resolvedConfig, config, raw.features());
    }

    private TaskDefinition buildDefinition(TaskRawData raw, Map<String, Object> resolvedConfig, TaskConfigRecord config, List<FeatureDefinition> features) {
        return TaskDefinition.builder()
                .nodeId(new NodeId(raw.taskId()))
                .version(raw.version())
                .name(raw.taskId())
                .type(raw.taskType())
                .timeoutMs(Objects.requireNonNullElse(config.timeoutMs(), DEFAULT_TIMEOUT))
                .config(resolvedConfig)
                .features(features)
                .ref(raw.taskId())
                .selectorExpression(raw.selectorExpression())
                .criticality(Objects.requireNonNullElse(raw.criticality(), 100))
                .requires(mapToSpecs(raw.requires()))
                .produces(mapToSpecs(raw.produces()))
                .responseSchema(raw.responseSchema())
                .failFast(Objects.requireNonNullElse(config.failFast(), true))
                .global(Objects.requireNonNullElse(config.global(), false))
                .refreshIntervalMs(Objects.requireNonNullElse(config.refreshIntervalMs(), 0L))
                .ttlMs(Objects.requireNonNullElse(config.ttlMs(), 0L))
                .build();
    }

    private List<DataSpec> mapToSpecs(List<DataMapping> mappings) {
        if (mappings == null) return List.of();
        return mappings.stream()
                .map(DataSpec::fromMapping)
                .toList();
    }

    private TaskConfigRecord parseConfig(Map<String, Object> configMap, String taskId) {
        if (configMap == null || configMap.isEmpty()) {
            return new TaskConfigRecord(DEFAULT_TIMEOUT, true, false, 0L, 0L);
        }
        try {
            return objectMapper.convertValue(configMap, TaskConfigRecord.class);
        } catch (Exception e) {
            log.warn("Falha ao converter configuração para a task '{}'. Usando defaults.", taskId);
            return new TaskConfigRecord(DEFAULT_TIMEOUT, true, false, 0L, 0L);
        }
    }

    record TaskConfigRecord(
        Long timeoutMs,
        Boolean failFast,
        Boolean global,
        Long refreshIntervalMs,
        Long ttlMs
    ) {}
}
