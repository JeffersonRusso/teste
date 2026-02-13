package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpTask extends AbstractTask {

    private final HttpRequestFactory requestFactory;
    private final HttpExecutor executor;
    private final HttpTaskConfiguration config;

    public HttpTask(TaskDefinition definition,
                    HttpRequestFactory requestFactory,
                    HttpExecutor executor) {
        super(definition);
        this.requestFactory = requestFactory;
        this.executor = executor;
        this.config = parseConfiguration(definition.getConfig());
    }

    private HttpTaskConfiguration parseConfiguration(JsonNode jsonConfig) {
        if (jsonConfig == null || jsonConfig.isMissingNode()) {
            throw new TaskConfigurationException("Configuração ausente para HttpTask: " + definition.getNodeId());
        }

        String url = jsonConfig.has("url") ? jsonConfig.get("url").asText() : null;
        String method = jsonConfig.has("method") ? jsonConfig.get("method").asText() : "GET";
        JsonNode body = jsonConfig.get("body");
        JsonNode headers = jsonConfig.get("headers");

        if (url == null || url.isBlank()) {
            throw new TaskConfigurationException("URL obrigatória para HttpTask: " + definition.getNodeId());
        }

        return new HttpTaskConfiguration(url, method, body, headers);
    }

    @Override
    public void validateConfig() {
        if (config.method() == null || config.method().isBlank()) {
            throw new TaskConfigurationException("Config 'method' é obrigatória para a task: " + definition.getNodeId());
        }
    }

    @Override
    public void execute(TaskData data) {
        // O timeout é gerenciado pela infraestrutura do pipeline, mas podemos passar o valor da definição
        long timeoutMs = definition.getTimeoutMs();

        OrchestratorRequest request = requestFactory.create(
                config.urlTemplate(),
                config.method(),
                timeoutMs,
                config.bodyConfig(),
                config.headersConfig(),
                data
        );

        // O executor precisa do TaskData para registrar o status e o body
        executor.execute(request, definition, data);
    }
}
