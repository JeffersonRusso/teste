package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import br.com.orquestrator.orquestrator.core.pipeline.TaskGraphBuilder;
import br.com.orquestrator.orquestrator.core.pipeline.TreeShaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class TreeShakingStep implements CompilationStep {
    private final TaskGraphBuilder graphBuilder;
    private final TreeShaker treeShaker;

    @Override public int getOrder() { return 20; }

    @Override
    public CompilationSession execute(CompilationSession session) {
        var graph = graphBuilder.build(session.getTasks());
        var optimized = treeShaker.optimize(graph, session.getDefinition().defaultRequiredOutputs());
        session.setTasks(new ArrayList<>(optimized));
        return session;
    }
}
