package br.com.orquestrator.orquestrator.domain.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDefinitionFactory {

    private final ObjectMapper objectMapper;
    private final Environment environment;
    
    // Regex para capturar #{ @environment.getProperty('chave') }
    // Suporta aspas simples ou duplas
    private static final Pattern ENV_PATTERN = Pattern.compile("#\\{\\s*@environment\\.getProperty\\(['\"]([^'\"]+)['\"]\\)\\s*}");

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
        JsonNode preProcessedConfig = preProcessConfig(configJson);
        TaskConfigRecord config = parseConfig(preProcessedConfig);
        
        if (config.timeoutMs() == null) {
            throw new IllegalArgumentException("Configuração obrigatória 'timeoutMs' ausente para a task: " + taskId);
        }
        
        FeaturePhases mergedFeatures = mergeFeatures(taskFeatures, profileFeaturesJson, taskId);

        return TaskDefinition.builder()
                .nodeId(new NodeId(taskId))
                .version(version)
                .name(taskId)
                .type(taskType)
                .timeoutMs(config.timeoutMs())
                .config(preProcessedConfig)
                .features(mergedFeatures)
                .ref(taskId)
                .selectorExpression(selectorExpression)
                .criticality(criticality != null ? criticality : 100)
                .requires(mapDataSpecs(requiresJson))
                .produces(mapDataSpecs(producesJson))
                .responseSchema(responseSchema)
                .failFast(config.failFast() != null ? config.failFast() : true)
                .global(config.global() != null ? config.global() : false)
                .refreshIntervalMs(config.refreshIntervalMs() != null ? config.refreshIntervalMs() : 0)
                .ttlMs(config.ttlMs() != null ? config.ttlMs() : 0)
                .build();
    }

    private JsonNode preProcessConfig(JsonNode config) {
        if (config == null || !config.isObject()) return config;

        ObjectNode newConfig = config.deepCopy();
        Iterator<Map.Entry<String, JsonNode>> fields = newConfig.fields();
        
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode valueNode = field.getValue();
            
            if (valueNode.isTextual()) {
                String value = valueNode.asText();
                if (value.contains("@environment")) {
                    String resolved = resolveEnvVars(value);
                    newConfig.set(field.getKey(), new TextNode(resolved));
                }
            } else if (valueNode.isObject()) {
                // Recursão para objetos aninhados (ex: headers)
                newConfig.set(field.getKey(), preProcessConfig(valueNode));
            }
        }
        return newConfig;
    }
    
    private String resolveEnvVars(String input) {
        Matcher matcher = ENV_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        
        while (matcher.find()) {
            String propertyKey = matcher.group(1);
            String propertyValue = environment.getProperty(propertyKey);
            
            if (propertyValue == null) {
                log.warn("Propriedade de ambiente não encontrada: {}", propertyKey);
                propertyValue = ""; // Ou manter o placeholder? Melhor falhar ou avisar.
            }
            
            matcher.appendReplacement(sb, propertyValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private FeaturePhases mergeFeatures(FeaturePhases taskFeatures, JsonNode profileFeaturesJson, String taskId) {
        try {
            FeaturePhases profileFeatures = profileFeaturesJson != null ? 
                objectMapper.treeToValue(profileFeaturesJson, FeaturePhases.class) : null;

            if (profileFeatures == null) {
                return taskFeatures;
            }

            List<FeatureDefinition> monitors = new ArrayList<>();
            addIfPresent(monitors, profileFeatures.monitors());
            addIfPresent(monitors, taskFeatures != null ? taskFeatures.monitors() : null);
            
            List<FeatureDefinition> pre = new ArrayList<>();
            addIfPresent(pre, profileFeatures.preExecution());
            addIfPresent(pre, taskFeatures != null ? taskFeatures.preExecution() : null);
            
            List<FeatureDefinition> post = new ArrayList<>();
            addIfPresent(post, profileFeatures.postExecution());
            addIfPresent(post, taskFeatures != null ? taskFeatures.postExecution() : null);
            
            return new FeaturePhases(monitors, pre, post);
            
        } catch (Exception e) {
            log.error("Erro ao mesclar features para task {}", taskId, e);
            return taskFeatures;
        }
    }
    
    private void addIfPresent(List<FeatureDefinition> target, List<FeatureDefinition> source) {
        if (source != null) {
            target.addAll(source);
        }
    }

    private TaskConfigRecord parseConfig(JsonNode jsonConfig) {
        if (jsonConfig == null) {
            return new TaskConfigRecord(null, null, null, null, null);
        }
        return objectMapper.convertValue(jsonConfig, TaskConfigRecord.class);
    }

    private List<DataSpec> mapDataSpecs(JsonNode node) {
        if (node == null || !node.isArray()) return Collections.emptyList();
        
        List<DataSpec> specs = new ArrayList<>();
        for (JsonNode item : node) {
            if (item.isTextual()) {
                specs.add(DataSpec.of(item.asText()));
            } else if (item.isObject()) {
                String name = item.get("name").asText();
                String path = item.has("path") ? item.get("path").asText() : null;
                boolean optional = item.has("optional") && item.get("optional").asBoolean();
                String typeStr = item.has("type") ? item.get("type").asText("ANY") : "ANY";
                
                specs.add(new DataSpec(name, DataType.valueOf(typeStr), optional, path));
            }
        }
        return specs;
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
