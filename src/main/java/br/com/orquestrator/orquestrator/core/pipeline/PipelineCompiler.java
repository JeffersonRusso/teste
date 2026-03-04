package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.CompilationContext;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PipelineCompiler: Transforma definições em executáveis otimizados.
 * Utiliza o CompilationContext para acessar ferramentas de infraestrutura.
 */
@Component
@RequiredArgsConstructor
public class PipelineCompiler {

    private final TaskGraphBuilder graphBuilder;
    private final TreeShaker treeShaker;
    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler taskChainCompiler;
    private final CompilationContext context; // <--- Única dependência de infra

    public Pipeline compile(PipelineDefinition def, Set<String> activeTags) {
        var scenarioTasks = filterByScenario(def.tasks(), activeTags);
        var necessaryTasks = optimizeGraph(scenarioTasks, def.defaultRequiredOutputs());
        
        // Validação de Contrato via Contexto
        context.contractValidator().validate(def, necessaryTasks);

        var signalMesh = createSignalMesh(necessaryTasks);
        var normalizationPlan = context.marshaller().createNormalizationPlan(def.inputMapping());

        Map<String, Pipeline.TaskNode> taskNodes = necessaryTasks.stream()
                .map(taskDef -> compileNode(taskDef, signalMesh))
                .collect(Collectors.toUnmodifiableMap(Pipeline.TaskNode::nodeId, Function.identity()));

        return new Pipeline(taskNodes, Duration.ofMillis(def.timeoutMs()), def.defaultRequiredOutputs(), normalizationPlan);
    }

    private Map<String, CompletableFuture<Void>> createSignalMesh(List<TaskDefinition> tasks) {
        Map<String, CompletableFuture<Void>> mesh = new HashMap<>();
        for (var task : tasks) {
            if (task.outputs() != null) {
                task.outputs().values().forEach(out -> mesh.put(out, new CompletableFuture<>()));
            }
        }
        return mesh;
    }

    private Pipeline.TaskNode compileNode(TaskDefinition def, Map<String, CompletableFuture<Void>> mesh) {
        var coreTask = taskRegistry.getTask(def);
        var executableTask = taskChainCompiler.compile(coreTask, def);

        List<CompletableFuture<Void>> dependencies = def.inputs().values().stream()
                .map(mesh::get).filter(Objects::nonNull).toList();

        List<CompletableFuture<Void>> signalsToEmit = def.outputs().values().stream()
                .map(mesh::get).filter(Objects::nonNull).toList();

        return new Pipeline.TaskNode(
            executableTask, def.nodeId().value(), def.type(), 
            dependencies, signalsToEmit,
            def.failFast(), def.guardCondition(), def.activationTags(), def.timeoutMs()
        );
    }

    private List<TaskDefinition> filterByScenario(List<TaskDefinition> tasks, Set<String> activeTags) {
        Set<String> effectiveTags = (activeTags == null || activeTags.isEmpty()) ? Set.of("default") : activeTags;
        return tasks.stream()
                .filter(t -> t.activationTags() == null || t.activationTags().isEmpty() || !Collections.disjoint(t.activationTags(), effectiveTags))
                .toList();
    }

    private List<TaskDefinition> optimizeGraph(List<TaskDefinition> tasks, Set<String> requiredOutputs) {
        var graph = graphBuilder.build(tasks);
        return new ArrayList<>(treeShaker.optimize(graph, requiredOutputs));
    }
}
