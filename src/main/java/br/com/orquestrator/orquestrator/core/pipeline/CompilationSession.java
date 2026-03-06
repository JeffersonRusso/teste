package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CompilationSession {
    private final PipelineDefinition definition;
    private final Set<String> activeTags;
    
    private List<TaskDefinition> tasks;
    private Map<String, List<TaskDefinition>> fusionGroups = new HashMap<>();
    private Map<String, ExecutionNode> nodes = new HashMap<>();
    private List<DataMarshaller.NormalizationStep> normalizationPlan = new ArrayList<>(); // <--- NOVO

    public CompilationSession(PipelineDefinition definition, Set<String> activeTags) {
        this.definition = definition;
        this.activeTags = activeTags;
        this.tasks = new ArrayList<>(definition.tasks());
    }
}
