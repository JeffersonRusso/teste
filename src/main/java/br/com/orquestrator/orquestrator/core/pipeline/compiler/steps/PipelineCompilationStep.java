package br.com.orquestrator.orquestrator.core.pipeline.compiler.steps;

import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PipelineCompilationStep: Uma etapa de transformação no pipeline de compilação.
 */
@FunctionalInterface
public interface PipelineCompilationStep {
    Stream<TaskDefinition> execute(PipelineDefinition definition, Stream<TaskDefinition> tasks, Set<String> activeTags);
}
