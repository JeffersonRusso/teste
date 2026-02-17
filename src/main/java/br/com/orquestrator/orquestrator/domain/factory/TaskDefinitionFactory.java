package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.infra.config.ConfigVariableResolver;
import br.com.orquestrator.orquestrator.tasks.registry.factory.FeatureManager;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * TaskDefinitionFactory: Orquestra a montagem da definição da tarefa.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDefinitionFactory {

    private final ObjectMapper objectMapper;
    private final ConfigVariableResolver variableResolver;
    private final FeatureManager featureManager;

    public TaskDefinition create(TaskRawData raw) {
        // 1. Resolve variáveis de ambiente
        JsonNode resolvedConfig = variableResolver.resolve(raw.configJson());
        
        // 2. Extrai configuração técnica
        TaskConfigRecord config = parseConfig(resolvedConfig, raw.taskId());
        
        // 3. Gerencia features
        FeaturePhases finalFeatures = featureManager.mergeAndResolve(
            raw.taskFeatures(), 
            raw.profileFeaturesJson(), 
            raw.taskId()
        );

        // 4. Montagem final
        return buildDefinition(raw, resolvedConfig, config, finalFeatures);
    }

    private TaskDefinition buildDefinition(TaskRawData raw, JsonNode resolvedConfig, TaskConfigRecord config, FeaturePhases finalFeatures) {
        return TaskDefinition.builder()
                .nodeId(new NodeId(raw.taskId()))
                .version(raw.version())
                .name(raw.taskId())
                .type(raw.taskType())
                .timeoutMs(config.timeoutMs())
                .config(resolvedConfig)
                .features(finalFeatures)
                .ref(raw.taskId())
                .selectorExpression(raw.selectorExpression())
                .criticality(Objects.requireNonNullElse(raw.criticality(), 100))
                .requires(DataSpecParser.parse(raw.requiresJson()))
                .produces(DataSpecParser.parse(raw.producesJson()))
                .responseSchema(raw.responseSchema())
                .failFast(Objects.requireNonNullElse(config.failFast(), true))
                .global(Objects.requireNonNullElse(config.global(), false))
                .refreshIntervalMs(Objects.requireNonNullElse(config.refreshIntervalMs(), 0L))
                .ttlMs(Objects.requireNonNullElse(config.ttlMs(), 0L))
                .build();
    }

    private TaskConfigRecord parseConfig(JsonNode json, String taskId) {
        return Optional.ofNullable(json)
                .filter(n -> !n.isMissingNode())
                .map(n -> objectMapper.convertValue(n, TaskConfigRecord.class))
                .map(record -> {
                    if (record.timeoutMs() == null) {
                        throw new IllegalArgumentException(STR."Timeout obrigatório ausente para a task: \{taskId}");
                    }
                    return record;
                })
                .orElse(new TaskConfigRecord(null, null, null, null, null));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TaskConfigRecord(
        Long timeoutMs,
        Boolean failFast,
        Boolean global,
        Long refreshIntervalMs,
        Long ttlMs
    ) {}
}
