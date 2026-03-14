package br.com.orquestrator.orquestrator.tasks.script.aviator;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

@Component
public class AviatorTaskProvider extends AbstractTaskProvider<AviatorTaskConfiguration> {

    private final DataFactory dataFactory;

    public AviatorTaskProvider(TaskBindingResolver bindingResolver, DataFactory dataFactory) {
        super(bindingResolver, AviatorTaskConfiguration.class);
        this.dataFactory = dataFactory;
    }

    @Override public String getType() { return "AVIATOR"; }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<AviatorTaskConfiguration> config) {
        return new AviatorTask(config, dataFactory);
    }
}
