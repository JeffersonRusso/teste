package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class TaskDefinition {
    
    private final NodeId nodeId;
    private final Integer version;
    private final String name;
    private final String type;
    private final long timeoutMs;
    private final Map<String, Object> config; // Expurgado JsonNode
    private final List<FeatureDefinition> features;
    private final String ref;
    private final boolean failFast;
    private final String selectorExpression;
    private final int criticality;
    private final boolean global;
    private final long refreshIntervalMs;
    private final long ttlMs;
    
    private final Map<String, Object> responseSchema; // Expurgado JsonNode

    private final List<DataSpec> requires;
    private final List<DataSpec> produces;

    @Builder
    public TaskDefinition(NodeId nodeId, Integer version, String name, String type, long timeoutMs,
                          Map<String, Object> config, List<FeatureDefinition> features, String ref, 
                          boolean failFast, String selectorExpression, int criticality,
                          boolean global, long refreshIntervalMs, long ttlMs,
                          Map<String, Object> responseSchema,
                          List<DataSpec> requires, List<DataSpec> produces) {
        this.nodeId = nodeId;
        this.version = version != null ? version : 1;
        this.name = name;
        this.type = type;
        this.timeoutMs = timeoutMs;
        this.config = config;
        this.features = features;
        this.ref = ref;
        this.failFast = failFast;
        this.selectorExpression = selectorExpression;
        this.criticality = criticality;
        this.global = global;
        this.refreshIntervalMs = refreshIntervalMs;
        this.ttlMs = ttlMs;
        this.responseSchema = responseSchema;
        this.requires = requires != null ? List.copyOf(requires) : List.of();
        this.produces = produces != null ? List.copyOf(produces) : List.of();
    }

    public List<FeatureDefinition> getAllFeaturesOrdered() {
        return features != null ? features : List.of();
    }
}
