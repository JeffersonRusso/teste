package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.input.ExecutePipelineUseCase;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component("PIPELINE") // Nomeado
public class PipelineTaskProvider extends AbstractTaskProvider<PipelineTaskConfiguration> {

    private final ObjectProvider<ExecutePipelineUseCase> pipelineUseCaseProvider;
    private final DataFactory dataFactory;

    public PipelineTaskProvider(TaskBindingResolver bindingResolver, 
                                ObjectProvider<ExecutePipelineUseCase> pipelineUseCaseProvider, 
                                DataFactory dataFactory) {
        super(bindingResolver, PipelineTaskConfiguration.class);
        this.pipelineUseCaseProvider = pipelineUseCaseProvider;
        this.dataFactory = dataFactory;
    }

    @Override public String getType() { return "PIPELINE"; }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<PipelineTaskConfiguration> config) {
        return new PipelineTask(
            config,
            command -> pipelineUseCaseProvider.getIfAvailable().execute(command), 
            dataFactory
        );
    }
}
