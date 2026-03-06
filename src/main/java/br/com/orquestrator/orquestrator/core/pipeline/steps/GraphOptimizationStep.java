package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import br.com.orquestrator.orquestrator.core.pipeline.GraphOptimizer;
import br.com.orquestrator.orquestrator.core.pipeline.TaskGraphBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraphOptimizationStep implements CompilationStep {
    private final TaskGraphBuilder graphBuilder;
    private final GraphOptimizer graphOptimizer;

    @Override public int getOrder() { return 30; }

    @Override
    public void execute(CompilationSession session) {
        var graph = graphBuilder.build(session.getTasks());
        session.setFusionGroups(graphOptimizer.identifyFusionGroups(graph));
    }
}
