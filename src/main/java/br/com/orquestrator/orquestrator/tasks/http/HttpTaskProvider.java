package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Provedor para tarefas HTTP.
 */
@Component("HTTP")
public class HttpTaskProvider extends TypedTaskProvider<HttpTaskConfiguration> {

    private final ExpressionService expressionService;
    private final HttpExecutor executor;

    public HttpTaskProvider(ObjectMapper objectMapper, ExpressionService expressionService, HttpExecutor executor) {
        super(objectMapper, HttpTaskConfiguration.class, "HTTP");
        this.expressionService = expressionService;
        this.executor = executor;
    }

    @Override
    protected Task createInternal(TaskDefinition def, HttpTaskConfiguration config) {
        // Chamada explícita ao construtor de 4 argumentos de HttpTask
        return new HttpTask(
            this.expressionService, 
            config, 
            this.executor, 
            def.getNodeId().value()
        );
    }
}
