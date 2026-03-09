package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.vo.SignalBinding;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TaskDefinition: Definição imutável de uma tarefa no pipeline.
 * Agora usa SignalBinding para inputs tipados.
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
    Map<String, SignalBinding> inputs, // Tipado!
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
