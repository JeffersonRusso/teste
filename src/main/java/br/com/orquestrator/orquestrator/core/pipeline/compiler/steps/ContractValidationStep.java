package br.com.orquestrator.orquestrator.core.pipeline.compiler.steps;

import br.com.orquestrator.orquestrator.core.engine.runtime.SignalSchema;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ContractValidationStep implements PipelineCompilationStep {

    private final ContractValidator contractValidator;

    @Override
    public Stream<TaskDefinition> execute(PipelineDefinition definition, Stream<TaskDefinition> tasks, Set<String> activeTags) {
        var taskList = tasks.collect(Collectors.toList());
        
        SignalSchema schema = SignalSchema.from(taskList);
        contractValidator.validate(definition, schema);
        
        return taskList.stream();
    }
}
