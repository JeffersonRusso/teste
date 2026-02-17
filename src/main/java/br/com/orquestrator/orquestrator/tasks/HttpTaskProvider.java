package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import br.com.orquestrator.orquestrator.tasks.http.HttpExecutor;
import br.com.orquestrator.orquestrator.tasks.http.HttpTask;
import br.com.orquestrator.orquestrator.tasks.http.HttpTaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class HttpTaskProvider extends TypedTaskProvider<HttpTaskConfiguration> {

    private final HttpExecutor executor;
    private final ExpressionService expressionService;

    public HttpTaskProvider(ObjectMapper objectMapper, HttpExecutor executor, ExpressionService expressionService) {
        super(objectMapper, HttpTaskConfiguration.class, "HTTP");
        this.executor = executor;
        this.expressionService = expressionService;
    }

    @Override
    protected Task createInternal(TaskDefinition def, HttpTaskConfiguration config) {
        // Corrigida a ordem dos parâmetros para bater com o construtor da HttpTask
        return new HttpTask(expressionService, config, executor);
    }
}
