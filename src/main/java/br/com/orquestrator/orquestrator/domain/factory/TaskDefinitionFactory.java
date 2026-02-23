package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.DataMapping;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.infra.config.ConfigVariableResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

@Component
public class TaskDefinitionFactory {

    private static final Logger LOGGER = System.getLogger(TaskDefinitionFactory.class.getName());
    private final ObjectMapper objectMapper;
    private final ConfigVariableResolver variableResolver;

    private static final long DEFAULT_TIMEOUT = 2000L;

    public TaskDefinitionFactory(ObjectMapper objectMapper, ConfigVariableResolver variableResolver) {
        this.objectMapper = objectMapper;
        this.variableResolver = variableResolver;
    }

    public TaskDefinition create(TaskRawData raw) {
        // 1. Resolve variáveis de ambiente no Map de configuração
        Map<String, Object> resolvedConfig = variableResolver.resolve(raw.config());
        
        // 2. Extrai configuração técnica básica usando o ObjectMapper para converter o Map
        TaskConfigRecord config = parseConfig(resolvedConfig, raw.taskId());
        
        // 3. Montagem final
        return buildDefinition(raw, resolvedConfig, config, raw.features());
    }

    private TaskDefinition buildDefinition(TaskRawData raw, Map<String, Object> resolvedConfig, TaskConfigRecord config, List<FeatureDefinition> features) {
        return new TaskDefinition(
                new NodeId(raw.taskId()),
                raw.version(),
                raw.taskId(),
                raw.taskType(),
                Objects.requireNonNullElse(config.timeoutMs(), DEFAULT_TIMEOUT),
                resolvedConfig,
                features,
                raw.taskId(),
                Objects.requireNonNullElse(config.failFast(), true),
                raw.selectorExpression(),
                Objects.requireNonNullElse(raw.criticality(), 100),
                Objects.requireNonNullElse(config.global(), false),
                Objects.requireNonNullElse(config.refreshIntervalMs(), 0L),
                Objects.requireNonNullElse(config.ttlMs(), 0L),
                raw.responseSchema(),
                mapToSpecs(raw.requires()),
                mapToSpecs(raw.produces())
        );
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
            LOGGER.log(Level.WARNING, "Falha ao converter configuração para a task {0}. Usando defaults.", taskId);
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
