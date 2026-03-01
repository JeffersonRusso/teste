package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PipelineCompiler {

    private final TaskGraphBuilder graphBuilder;
    private final TreeShaker treeShaker;
    private final ContractValidator contractValidator;
    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler taskChainCompiler;

    public Pipeline compile(PipelineDefinition def, Set<String> activeTags) {
        var scenarioTasks = filterByTags(def.tasks(), activeTags);
        var graph = graphBuilder.build(scenarioTasks);
        var necessaryTasks = treeShaker.optimize(graph, def.defaultRequiredOutputs());
        List<TaskDefinition> finalTasks = new ArrayList<>(necessaryTasks);

        contractValidator.validate(def, finalTasks);

        Map<String, Pipeline.TaskNode> taskNodes = finalTasks.stream()
                .map(this::compileNode)
                .collect(Collectors.toUnmodifiableMap(Pipeline.TaskNode::nodeId, Function.identity()));

        return new Pipeline(
                taskNodes,
                Duration.ofMillis(def.timeoutMs()),
                def.defaultRequiredOutputs(),
                def.inputMapping()
        );
    }

    private List<TaskDefinition> filterByTags(List<TaskDefinition> tasks, Set<String> activeTags) {
        Set<String> effectiveTags = (activeTags == null || activeTags.isEmpty()) ? Set.of("default") : activeTags;
        return tasks.stream()
                .filter(t -> t.activationTags() == null || t.activationTags().isEmpty() || !Collections.disjoint(t.activationTags(), effectiveTags))
                .toList();
    }

    private Pipeline.TaskNode compileNode(TaskDefinition def) {
        var coreTask = taskRegistry.getTask(def);
        var decoratedTask = taskChainCompiler.compile(coreTask, def);

        var inputs = def.inputs().values().stream()
                .map(s -> new Pipeline.InputInstruction(s, true))
                .toList();

        var outputs = def.outputs().values().stream()
                .map(Pipeline.OutputInstruction::new)
                .toList();

        return new Pipeline.TaskNode(
            decoratedTask, def.nodeId().value(), def.type(), inputs, outputs,
            def.failFast(), def.guardCondition(), def.activationTags(), def.timeoutMs()
        );
    }
}
