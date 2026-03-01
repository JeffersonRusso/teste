package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TaskDefinition: Definição imutável de uma tarefa.
 * Refatorado para refletir o novo schema Data-Driven (Rede de Petri).
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
    
    // Data-Driven: Inputs e Outputs como Mapas
    // Inputs: { "param_name": "context_key" }
    Map<String, String> inputs,
    // Outputs: { "result_name": "context_key" }
    Map<String, String> outputs,
    
    // Controle de Fluxo
    Set<String> activationTags,
    String guardCondition,
    
    // Legado (para compatibilidade se necessário, mas idealmente removido)
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

    public List<FeatureDefinition> getAllFeaturesOrdered() {
        return features;
    }
    
    // Métodos de conveniência
    public NodeId getNodeId() { return nodeId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isFailFast() { return failFast; }
}
