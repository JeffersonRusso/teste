package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Task responsável por realizar chamadas HTTP.
 * Centraliza a lógica de resolução de templates (URL, Body, Headers) antes da execução.
 */
@Slf4j
public class HttpTask extends AbstractTask {

    private final HttpExecutor executor;
    private final HttpTaskConfiguration config;
    private final ExpressionService expressionService;

    public HttpTask(TaskDefinition definition,
                    HttpExecutor executor,
                    HttpTaskConfiguration config,
                    ExpressionService expressionService) {
        super(definition);
        this.executor = executor;
        this.config = config;
        this.expressionService = expressionService;
    }

    @Override
    public void validateConfig() {
        if (config.urlTemplate() == null || config.urlTemplate().isBlank()) {
            throw new TaskConfigurationException("URL obrigatória para HttpTask: " + definition.getNodeId());
        }
    }

    @Override
    public void execute(TaskData data) {
        EvaluationContext evalContext = expressionService.create(data);

        // 1. Resolve URL
        String url = evalContext.resolve(config.urlTemplate(), String.class);

        // 2. Resolve Body (como String/JSON)
        String body = config.bodyTemplate() != null ? 
                      evalContext.resolve(config.bodyTemplate(), String.class) : null;

        // 3. Resolve Headers
        Map<String, String> headers = resolveHeaders(evalContext);

        // 4. Cria a Intenção de Requisição
        OrchestratorRequest request = new OrchestratorRequest(
                config.method(),
                URI.create(url),
                headers,
                body,
                definition.getTimeoutMs()
        );

        // 5. Delega a execução para a infraestrutura
        executor.execute(request, definition, data);
    }

    private Map<String, String> resolveHeaders(EvaluationContext context) {
        Map<String, String> resolved = new HashMap<>();
        
        // Headers padrão
        resolved.put("Content-Type", "application/json");
        resolved.put("Accept", "application/json");

        // Headers configurados (sobrescrevem os padrões se necessário)
        if (config.headersTemplates() != null) {
            config.headersTemplates().forEach((key, template) -> 
                resolved.put(key, context.resolve(template, String.class))
            );
        }
        return resolved;
    }
}
