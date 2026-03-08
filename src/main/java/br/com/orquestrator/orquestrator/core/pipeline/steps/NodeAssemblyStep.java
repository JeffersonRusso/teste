package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import br.com.orquestrator.orquestrator.core.pipeline.NodeAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * NodeAssemblyStep: Transforma as definições de tarefas em nós executáveis.
 * Agora simplificado para o modelo de Dataflow.
 */
@Component
@RequiredArgsConstructor
public class NodeAssemblyStep implements CompilationStep {

    private final NodeAssembler nodeAssembler;

    @Override public int getOrder() { return 40; }

    @Override
    public CompilationSession execute(CompilationSession session) {
        Map<String, ExecutionNode> nodes = new HashMap<>();
        
        Set<String> activeProducedKeys = new HashSet<>();
        session.getTasks().forEach(t -> activeProducedKeys.addAll(t.outputs().values()));

        for (var taskDef : session.getTasks()) {
            String nodeId = taskDef.nodeId().value();
            nodes.put(nodeId, nodeAssembler.assemble(taskDef, activeProducedKeys));
        }

        session.setNodes(nodes);
        return session;
    }
}
