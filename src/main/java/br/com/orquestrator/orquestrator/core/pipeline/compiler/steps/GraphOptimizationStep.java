package br.com.orquestrator.orquestrator.core.pipeline.compiler.steps;

import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Stream;

@Component
public class GraphOptimizationStep implements PipelineCompilationStep {
    @Override
    public Stream<TaskDefinition> execute(PipelineDefinition definition, Stream<TaskDefinition> tasks, Set<String> activeTags) {
        return tasks;
    }
}
