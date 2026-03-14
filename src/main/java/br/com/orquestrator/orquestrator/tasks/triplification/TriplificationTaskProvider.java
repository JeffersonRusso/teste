package br.com.orquestrator.orquestrator.tasks.triplification;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;

/**
 * TriplificationTaskProvider: Provedor para a tarefa de triplificação RDF.
 */
@Component("TRIPLIFICATION")
public class TriplificationTaskProvider extends AbstractTaskProvider<TriplificationTaskConfiguration> {

    private final DataFactory dataFactory;

    public TriplificationTaskProvider(TaskBindingResolver bindingResolver, DataFactory dataFactory) {
        super(bindingResolver, TriplificationTaskConfiguration.class);
        this.dataFactory = dataFactory;
    }

    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<TriplificationTaskConfiguration> config) {
        // Resolve a configuração
        TriplificationTaskConfiguration resolved = config.resolve(definition.config());

        // Fornece um base_uri padrão se não estiver configurado
        String baseUri = resolved.baseUri() != null ? resolved.baseUri() : "http://antifraude.com.br/ontology#";

        return new TriplificationTask(baseUri, dataFactory);
    }
}
