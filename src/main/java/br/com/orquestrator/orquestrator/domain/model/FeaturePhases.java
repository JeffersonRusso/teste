package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeaturePhases(
    List<FeatureDefinition> monitors,
    List<FeatureDefinition> preExecution,
    List<FeatureDefinition> postExecution
) {
    public FeaturePhases {
        monitors = (monitors != null) ? List.copyOf(monitors) : Collections.emptyList();
        preExecution = (preExecution != null) ? List.copyOf(preExecution) : Collections.emptyList();
        postExecution = (postExecution != null) ? List.copyOf(postExecution) : Collections.emptyList();
    }
}
