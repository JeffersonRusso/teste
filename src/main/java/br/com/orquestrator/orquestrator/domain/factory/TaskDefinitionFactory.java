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

/**
 * Fábrica de TaskDefinition (Maestro).
 * Responsável pela orquestração da montagem da definição da tarefa, delegando o parsing
 * e o gerenciamento de features para especialistas.
 * Java 21: Utiliza String Templates e Objects.requireNonNullElse para clareza.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDefinitionFactory {

    private final ObjectMapper objectMapper;
    private final ConfigVariableResolver variableResolver;
    private final FeatureManager featureManager;

    public TaskDefinition create(
            String taskId,
            Integer version,
            String taskType,
            JsonNode configJson,
            FeaturePhases taskFeatures,
            JsonNode profileFeaturesJson,
            String selectorExpression,
            Integer criticality,
            JsonNode requiresJson,
            JsonNode producesJson,
            JsonNode responseSchema
    ) {
        // 1. Resolve variáveis de ambiente no JSON de configuração
        JsonNode resolvedConfig = variableResolver.resolve(configJson);
        
        // 2. Extrai a configuração técnica (timeout, failFast, etc)
        TaskConfigRecord config = parseConfig(resolvedConfig, taskId);
        
        // 3. Delega o gerenciamento de features (Merge + Resolução de Templates)
        FeaturePhases finalFeatures = featureManager.mergeAndResolve(taskFeatures, profileFeaturesJson, taskId);

        // 4. Montagem final do objeto de domínio
        return TaskDefinition.builder()
                .nodeId(new NodeId(taskId))
                .version(version)
                .name(taskId)
                .type(taskType)
                .timeoutMs(config.timeoutMs())
                .config(resolvedConfig)
                .features(finalFeatures)
                .ref(taskId)
                .selectorExpression(selectorExpression)
                .criticality(Objects.requireNonNullElse(criticality, 100))
                .requires(DataSpecParser.parse(requiresJson))
                .produces(DataSpecParser.parse(producesJson))
                .responseSchema(responseSchema)
                .failFast(Objects.requireNonNullElse(config.failFast(), true))
                .global(Objects.requireNonNullElse(config.global(), false))
                .refreshIntervalMs(Objects.requireNonNullElse(config.refreshIntervalMs(), 0L))
                .ttlMs(Objects.requireNonNullElse(config.ttlMs(), 0L))
                .build();
    }

    private TaskConfigRecord parseConfig(JsonNode json, String taskId) {
        if (json == null || json.isMissingNode()) {
            return new TaskConfigRecord(null, null, null, null, null);
        }
        
        TaskConfigRecord record = objectMapper.convertValue(json, TaskConfigRecord.class);
        
        if (record.timeoutMs() == null) {
             throw new IllegalArgumentException(STR."Timeout obrigatório ausente para a task: \{taskId}");
        }
        
        return record;
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
