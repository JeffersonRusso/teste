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

/**
 * NodeAssembler: Responsável por transformar definições de tarefas em nós executáveis.
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

        List<String> dependencies = def.inputs().values().stream()
                .map(val -> DataPath.of(val).getRoot())
                .filter(taskProducedKeys::contains)
                .toList();

        List<String> signalsToEmit = def.outputs().values().stream().toList();

        ExecutionNode node = new DefaultExecutionNode(def.nodeId().value(), executableTask, dependencies, signalsToEmit);

        if (def.guardCondition() != null && !def.guardCondition().isBlank()) {
            node = new GuardExecutionNode(node, def.guardCondition(), context.expressionEngine());
        }

        return node;
    }

    public ExecutionNode assembleFused(List<TaskDefinition> group, Set<String> taskProducedKeys) {
        List<Task> executables = group.stream()
                .map(def -> taskChainCompiler.compile(taskRegistry.getTask(def), def))
                .toList();

        TaskDefinition leader = group.getFirst();

        // 1. Dependências do líder (entrada do grupo)
        List<String> dependencies = leader.inputs().values().stream()
                .map(val -> DataPath.of(val).getRoot())
                .filter(taskProducedKeys::contains)
                .toList();

        // 2. Sinais de saída: Agrega os outputs de TODAS as tasks do grupo
        // Isso garante que dependentes externos de qualquer task interna sejam liberados.
        List<String> allSignalsToEmit = new ArrayList<>();
        group.forEach(def -> allSignalsToEmit.addAll(def.outputs().values()));

        return new DefaultExecutionNode(leader.nodeId().value(), new FusedTask(executables), dependencies, allSignalsToEmit);
    }
}
