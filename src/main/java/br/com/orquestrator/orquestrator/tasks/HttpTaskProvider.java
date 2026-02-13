package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.http.HttpExecutor;
import br.com.orquestrator.orquestrator.tasks.http.HttpTask;
import br.com.orquestrator.orquestrator.tasks.http.HttpTaskConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Provedor de HttpTask que atua como o tradutor entre o JSON de configuração e a Task tipada.
 */
@Component
@RequiredArgsConstructor
public class HttpTaskProvider implements TaskProvider {

    private final HttpExecutor executor;
    private final ExpressionService expressionService;

    @Override
    public String getType() {
        return "HTTP";
    }

    @Override
    public Task create(TaskDefinition def) {
        HttpTaskConfiguration config = parseConfig(def.getConfig(), def.getNodeId().value());
        return new HttpTask(def, executor, config, expressionService);
    }

    private HttpTaskConfiguration parseConfig(JsonNode json, String nodeId) {
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração ausente para HttpTask: " + nodeId);
        }

        String url = json.path("url").asText(null);
        String method = json.path("method").asText("GET");
        
        // O body agora é tratado como uma String (Template) no Record
        String bodyTemplate = json.has("body") ? json.get("body").toString() : null;
        
        Map<String, String> headers = new HashMap<>();
        JsonNode headersNode = json.get("headers");
        if (headersNode != null && headersNode.isObject()) {
            headersNode.fields().forEachRemaining(entry -> 
                headers.put(entry.getKey(), entry.getValue().asText())
            );
        }

        return new HttpTaskConfiguration(url, method, bodyTemplate, headers);
    }
}
