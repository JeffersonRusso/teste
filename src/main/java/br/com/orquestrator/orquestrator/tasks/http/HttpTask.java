package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.BaseTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpTask: Executa chamadas HTTP baseadas em templates SpEL.
 */
public class HttpTask extends BaseTask<HttpTaskConfiguration> {

    private final HttpExecutor executor;
    private final String nodeId;

    public HttpTask(ExpressionService expressionService, HttpTaskConfiguration config, HttpExecutor executor, String nodeId) {
        super(expressionService, config);
        this.executor = executor;
        this.nodeId = nodeId;
    }

    @Override
    protected TaskResult executeInternal(ExecutionContext context, EvaluationContext eval) {
        String url = eval.resolve(config.urlTemplate(), String.class);
        String body = config.bodyTemplate() != null ? eval.resolve(config.bodyTemplate(), String.class) : null;
        
        Map<String, String> headers = new HashMap<>();
        if (config.headersTemplates() != null) {
            config.headersTemplates().forEach((k, v) -> headers.put(k, eval.resolve(v, String.class)));
        }

        long timeout = config.timeout() != null ? config.timeout().longValue() : 0L;

        return executor.execute(new OrchestratorRequest(config.method(), URI.create(url), headers, body, timeout), nodeId);
    }
}
