package br.com.orquestrator.orquestrator.core.pipeline.compiler.steps;

import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Stream;

/**
 * ScenarioFilterStep: Filtra as tasks com base nas tags ativas do contexto.
 */
@Slf4j
@Component
public class ScenarioFilterStep implements PipelineCompilationStep {

    @Override
    public Stream<TaskDefinition> execute(PipelineDefinition definition, Stream<TaskDefinition> tasks, Set<String> activeTags) {
        // TELL, DON'T ASK: Delegamos a decisão de ativação para a própria TaskDefinition.
        return tasks.filter(task -> task.isActiveFor(activeTags));
    }
}
