package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TaskDefinition: Definição imutável de uma tarefa no pipeline.
 */
public record TaskDefinition(
    NodeId nodeId,
    Integer version,
    String name,
    String type,
    long timeoutMs,
    Map<String, Object> config,
    List<FeatureDefinition> features,
    boolean failFast,
    Map<String, String> inputs,
    Map<String, String> outputs,
    Set<String> activationTags,
    String guardCondition,
    boolean global,
    long refreshIntervalMs
) {
    public TaskDefinition {
        version = version != null ? version : 1;
        features = features != null ? List.copyOf(features) : List.of();
        inputs = inputs != null ? Map.copyOf(inputs) : Map.of();
        outputs = outputs != null ? Map.copyOf(outputs) : Map.of();
        config = config != null ? Map.copyOf(config) : Map.of();
        activationTags = activationTags != null ? Set.copyOf(activationTags) : Set.of("default");
    }

    /**
     * Identifica quais campos a tarefa pretende produzir (baseado nas chaves de saída).
     * Útil para otimizações de extração (ex: HttpTask).
     */
    public Set<String> getRequiredFields() {
        if (outputs == null || outputs.isEmpty()) return Set.of(".");
        return outputs.keySet();
    }

    public boolean isCpuBound() {
        return "AVIATOR".equalsIgnoreCase(type) || 
               "GROOVY_SCRIPT".equalsIgnoreCase(type) || 
               "SPEL".equalsIgnoreCase(type);
    }

    public NodeId getNodeId() { return nodeId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isFailFast() { return failFast; }
}
