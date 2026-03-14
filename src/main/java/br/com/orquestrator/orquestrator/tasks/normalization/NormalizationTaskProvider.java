package br.com.orquestrator.orquestrator.tasks.normalization;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

/**
 * NormalizationTaskProvider: Provedor para a tarefa de normalização canônica.
 * Agora integrado ao padrão soberano de provedores.
 */
@Component("NORMALIZATION")
public class NormalizationTaskProvider extends AbstractTaskProvider<NormalizationTaskConfiguration> {

    private final DataFactory dataFactory;

    public NormalizationTaskProvider(TaskBindingResolver bindingResolver, DataFactory dataFactory) {
        super(bindingResolver, NormalizationTaskConfiguration.class);
        this.dataFactory = dataFactory;
    }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<NormalizationTaskConfiguration> config) {
        // Resolve a configuração rica (contendo o mapa de regras)
        NormalizationTaskConfiguration resolved = config.resolve(definition.config());
        
        return new NormalizationTask(resolved.rules(), dataFactory);
    }
}
