package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.engine.runtime.DefaultExecutionNode;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import br.com.orquestrator.orquestrator.core.engine.runtime.GuardExecutionNode;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.tasks.base.FusedTask;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.CompilationContext;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * NodeAssembler: Responsável por transformar definições de tarefas em nós executáveis.
 * OTIMIZADO: Sinais de dependência agora usam a RAIZ do path para garantir sincronismo com streaming.
 */
@Component
@RequiredArgsConstructor
public class NodeAssembler {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler taskChainCompiler;
    private final CompilationContext context;

    public ExecutionNode assemble(TaskDefinition def, Set<String> taskProducedKeys) {
        var coreTask = taskRegistry.getTask(def);
        var executableTask = taskChainCompiler.compile(coreTask, def);

        // Dependências: O que eu preciso para rodar (sempre pela raiz)
        List<String> dependencies = def.inputs().values().stream()
                .map(val -> DataPath.of(val).getRoot())
                .filter(root -> isAnyKeyProduced(root, taskProducedKeys))
                .distinct()
                .toList();

        // Sinais: O que eu libero quando termino (sempre pela raiz)
        List<String> signalsToEmit = def.outputs().values().stream()
                .map(val -> DataPath.of(val).getRoot())
                .distinct()
                .toList();

        ExecutionNode node = new DefaultExecutionNode(def.nodeId().value(), executableTask, dependencies, signalsToEmit);

        if (def.guardCondition() != null && !def.guardCondition().isBlank()) {
            node = new GuardExecutionNode(node, def.guardCondition(), context.expressionEngine());
        }

        return node;
    }

    /**
     * Verifica se a raiz ou qualquer sub-caminho dela é produzido por alguma task.
     */
    private boolean isAnyKeyProduced(String root, Set<String> producedKeys) {
        if (producedKeys.contains(root)) return true;
        return producedKeys.stream().anyMatch(key -> key.startsWith(root + "."));
    }

    public ExecutionNode assembleFused(List<TaskDefinition> group, Set<String> taskProducedKeys) {
        List<Task> executables = group.stream()
                .map(def -> taskChainCompiler.compile(taskRegistry.getTask(def), def))
                .toList();

        TaskDefinition leader = group.getFirst();

        List<String> dependencies = leader.inputs().values().stream()
                .map(val -> DataPath.of(val).getRoot())
                .filter(root -> isAnyKeyProduced(root, taskProducedKeys))
                .distinct()
                .toList();

        List<String> allSignalsToEmit = group.stream()
                .flatMap(def -> def.outputs().values().stream())
                .map(val -> DataPath.of(val).getRoot())
                .distinct()
                .toList();

        return new DefaultExecutionNode(leader.nodeId().value(), new FusedTask(executables), dependencies, allSignalsToEmit);
    }
}
