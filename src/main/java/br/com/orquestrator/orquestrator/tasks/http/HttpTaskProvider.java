package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.ports.output.AbstractTaskProvider;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("HTTP") // Nomeado para descoberta nativa
public class HttpTaskProvider extends AbstractTaskProvider<HttpTaskConfiguration> {

    private final RestClient restClient;
    private final DataFactory dataFactory;

    public HttpTaskProvider(TaskBindingResolver bindingResolver, RestClient restClient, DataFactory dataFactory) {
        super(bindingResolver, HttpTaskConfiguration.class);
        this.restClient = restClient;
        this.dataFactory = dataFactory;
    }


    @Override
    protected Task createTask(TaskDefinition definition, CompiledConfiguration<HttpTaskConfiguration> config) {
        return new HttpTask(restClient, dataFactory, config);
    }
}
