package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.engine.runtime.DefaultExecutionNode;
import br.com.orquestrator.orquestrator.core.engine.runtime.DefaultSignalProjector;
import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import br.com.orquestrator.orquestrator.core.engine.runtime.GuardExecutionNode;
import br.com.orquestrator.orquestrator.core.engine.runtime.SignalProjector;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * NodeAssembler: Responsável por transformar definições de tarefas em nós executáveis.
 * Atua como um Compilador de Grafo, pré-calculando dependências e projeções.
 */
@Component
@RequiredArgsConstructor
public class NodeAssembler {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler taskChainCompiler;
    private final ExpressionEngine expressionEngine;

    public ExecutionNode assemble(TaskDefinition def, Set<String> taskProducedKeys) {
        var coreTask = taskRegistry.getTask(def);
        var executableTask = taskChainCompiler.compile(coreTask, def);

        // 1. Compilação das Projeções (Soldagem de Sinais)
        SignalProjector inputProjector = DefaultSignalProjector.compile(def.inputs());
        SignalProjector outputProjector = DefaultSignalProjector.compile(def.outputs());

        // 2. Pré-cálculo de campos obrigatórios para a Task
        Set<String> requiredFields = def.getRequiredFields();

        // 3. Criação do Nó Base com templates prontos
        ExecutionNode node = new DefaultExecutionNode(
            def.nodeId().value(), 
            executableTask, 
            inputProjector, 
            outputProjector,
            requiredFields
        );

        // 4. Aplicação da Condição de Guarda
        if (def.guardCondition() != null && !def.guardCondition().isBlank()) {
            node = new GuardExecutionNode(node, def.guardCondition(), expressionEngine);
        }

        return node;
    }
}
