package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

@Component
public class GroovyTaskProvider extends AbstractTaskProvider<GroovyTaskConfiguration> {

    private final DataFactory dataFactory;

    public GroovyTaskProvider(TaskBindingResolver bindingResolver, DataFactory dataFactory) {
        super(bindingResolver, GroovyTaskConfiguration.class);
        this.dataFactory = dataFactory;
    }

    @Override public String getType() { return "GROOVY"; }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<GroovyTaskConfiguration> config) {
        return new GroovyTask(config, dataFactory);
    }
}
