package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import org.springframework.stereotype.Component;

@Component("SPEL") // Nomeado
public class SpelTaskProvider extends AbstractTaskProvider<SpelTaskConfiguration> {

    private final ExpressionEngine expressionEngine;
    private final DataFactory dataFactory;

    public SpelTaskProvider(TaskBindingResolver bindingResolver, ExpressionEngine expressionEngine, DataFactory dataFactory) {
        super(bindingResolver, SpelTaskConfiguration.class);
        this.expressionEngine = expressionEngine;
        this.dataFactory = dataFactory;
    }

    @Override public String getType() { return "SPEL"; }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<SpelTaskConfiguration> config) {
        return new SpelTask(expressionEngine, config, dataFactory);
    }
}
