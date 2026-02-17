package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpTask: Puramente funcional.
 */
@RequiredArgsConstructor
public class HttpTask implements Task {

    private final TaskDefinition definition;
    private final HttpExecutor executor;
    private final HttpTaskConfiguration config;
    private final ExpressionService expressionService;

    @Override
    public Object execute(ExecutionContext context) {
        var eval = expressionService.create(context);

        String url = eval.resolve(config.urlTemplate(), String.class);
        String body = config.bodyTemplate() != null ? eval.resolve(config.bodyTemplate(), String.class) : null;
        
        Map<String, String> headers = new HashMap<>();
        if (config.headersTemplates() != null) {
            config.headersTemplates().forEach((k, v) -> headers.put(k, eval.resolve(v, String.class)));
        }

        return executor.execute(new OrchestratorRequest(config.method(), URI.create(url), headers, body, definition.getTimeoutMs()), definition.getNodeId().value(), context);
    }
}
