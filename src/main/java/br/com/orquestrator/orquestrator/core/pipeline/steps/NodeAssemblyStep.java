package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.engine.runtime.ExecutionNode;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import br.com.orquestrator.orquestrator.core.pipeline.NodeAssembler;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeAssemblyStep implements CompilationStep {
    private final NodeAssembler nodeAssembler;

    @Override public int getOrder() { return 40; }

    @Override
    public void execute(CompilationSession session) {
        Map<String, ExecutionNode> nodes = new HashMap<>();
        Set<String> processed = new HashSet<>();
        
        // CRÍTICO: Apenas chaves produzidas por tasks que ESTÃO NESTA SESSÃO
        // podem ser consideradas dependências bloqueantes.
        Set<String> activeProducedKeys = new HashSet<>();
        session.getTasks().forEach(t -> activeProducedKeys.addAll(t.outputs().values()));

        for (var taskDef : session.getTasks()) {
            String nodeId = taskDef.nodeId().value();
            if (processed.contains(nodeId)) continue;

            if (session.getFusionGroups().containsKey(nodeId)) {
                var group = session.getFusionGroups().get(nodeId);
                nodes.put(nodeId, nodeAssembler.assembleFused(group, activeProducedKeys));
                group.forEach(t -> processed.add(t.nodeId().value()));
            } else {
                nodes.put(nodeId, nodeAssembler.assemble(taskDef, activeProducedKeys));
                processed.add(nodeId);
            }
        }
        session.setNodes(nodes);
    }
}
