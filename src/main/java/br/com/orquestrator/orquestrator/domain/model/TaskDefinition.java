package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.TaskContract;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;


@Getter
public class TaskDefinition {
    
    private final NodeId nodeId;
    private final Integer version;
    private final String name;
    private final String type;
    private final long timeoutMs;
    private final JsonNode config;
    private final FeaturePhases features;
    private final String ref;
    private final boolean failFast;
    private final String selectorExpression;
    private final int criticality;
    private final boolean global;
    private final long refreshIntervalMs;
    private final long ttlMs;
    
    private final JsonNode responseSchema;

    private final List<DataSpec> requires;
    private final List<DataSpec> produces;
    
    private final List<FeatureDefinition> allFeaturesOrdered;
    
    private final TaskContract contract;

    @Builder
    public TaskDefinition(NodeId nodeId, Integer version, String name, String type, long timeoutMs,
                          JsonNode config, FeaturePhases features, String ref, 
                          boolean failFast, String selectorExpression, int criticality,
                          boolean global, long refreshIntervalMs, long ttlMs,
                          JsonNode responseSchema,
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

        this.requires = requires != null ? List.copyOf(requires) : Collections.emptyList();
        this.produces = produces != null ? List.copyOf(produces) : Collections.emptyList();
        
        this.allFeaturesOrdered = buildFeaturesList(features);
        
        this.contract = new TaskContract(
            computeAllowedPaths(this.requires),
            extractNames(this.produces)
        );
    }
    
    private List<FeatureDefinition> buildFeaturesList(FeaturePhases features) {
        if (features == null) {
            return Collections.emptyList();
        }
        List<FeatureDefinition> ordered = new ArrayList<>();
        addIfPresent(ordered, features.monitors());
        addIfPresent(ordered, features.preExecution());
        addIfPresent(ordered, features.postExecution());
        return List.copyOf(ordered);
    }
    
    private void addIfPresent(List<FeatureDefinition> target, List<FeatureDefinition> source) {
        if (source != null && !source.isEmpty()) {
            target.addAll(source);
        }
    }

    private Set<String> computeAllowedPaths(List<DataSpec> specs) {
        Set<String> paths = new HashSet<>();
        for (DataSpec spec : specs) {
            String name = spec.name();
            paths.add(name);
            if (name.contains(".")) {
                String[] segments = name.split("\\.");
                StringJoiner current = new StringJoiner(".");
                for (String segment : segments) {
                    current.add(segment);
                    paths.add(current.toString());
                }
            }
        }
        return Collections.unmodifiableSet(paths);
    }

    private Set<String> extractNames(List<DataSpec> specs) {
        return specs.stream()
                .map(DataSpec::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean produces(String key) {
        return produces.stream().anyMatch(d -> d.name().equals(key));
    }
}
