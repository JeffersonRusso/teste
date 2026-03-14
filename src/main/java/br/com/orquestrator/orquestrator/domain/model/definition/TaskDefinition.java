package br.com.orquestrator.orquestrator.domain.model.definition;

import br.com.orquestrator.orquestrator.domain.model.TaskBehavior;
import br.com.orquestrator.orquestrator.domain.model.TaskInput;
import br.com.orquestrator.orquestrator.domain.model.TaskOutput;
import br.com.orquestrator.orquestrator.domain.model.vo.DataBinding;
import br.com.orquestrator.orquestrator.domain.model.vo.SignalName;
import br.com.orquestrator.orquestrator.domain.model.vo.NodeId;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TaskDefinition: Representação RICA e Tipada de uma tarefa.
 */
public record TaskDefinition(
    NodeId nodeId,
    String type,
    TaskBehavior behavior,
    List<FeatureDefinition> features,
    Map<String, Object> config,
    List<TaskInput> inputs,
    List<TaskOutput> outputs
) {

    public TaskDefinition {
        if (behavior == null) behavior = TaskBehavior.defaultBehavior();
        if (features == null) features = Collections.emptyList();
        if (config == null) config = Collections.emptyMap();
        if (inputs == null) inputs = Collections.emptyList();
        if (outputs == null) outputs = Collections.emptyList();
    }

    public boolean isActiveFor(Set<String> activeTags) { return behavior.matches(activeTags); }
    public boolean isFailFast() { return behavior.failFast(); }
    
    public Set<String> getProducedSignalNames() {
        return outputs.stream().map(TaskOutput::targetSignal).collect(Collectors.toSet());
    }

    public Set<String> getRequiredSignalNames() {
        return inputs.stream().map(TaskInput::sourceSignal).collect(Collectors.toSet());
    }

    public Map<String, DataBinding> getExecutionInputBindings() {
        Map<String, DataBinding> map = new HashMap<>();
        inputs.forEach(i -> map.put(i.localKey(), new DataBinding(i.sourceSignal(), i.sourcePath())));
        return map;
    }

    public Map<String, DataBinding> getExecutionOutputBindings() {
        return outputs.stream()
                .collect(Collectors.toMap(
                    TaskOutput::localKey, 
                    o -> new DataBinding(o.targetSignal(), "")
                ));
    }
}
