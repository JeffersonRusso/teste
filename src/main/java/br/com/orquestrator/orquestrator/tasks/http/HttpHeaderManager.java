package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpHeaderManager {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "Accept";
    private static final String DEFAULT_MIME_TYPE = "application/json";

    public OrchestratorRequest apply(OrchestratorRequest request, JsonNode config, EvaluationContext context) {
        Map<String, String> resolvedHeaders = resolveConfiguredHeaders(config, context);

        OrchestratorRequest updatedRequest = request;
        for (Map.Entry<String, String> entry : resolvedHeaders.entrySet()) {
            updatedRequest = updatedRequest.withHeader(entry.getKey(), entry.getValue());
        }

        updatedRequest = ensureDefaultHeader(updatedRequest, CONTENT_TYPE, DEFAULT_MIME_TYPE);
        updatedRequest = ensureDefaultHeader(updatedRequest, ACCEPT, DEFAULT_MIME_TYPE);

        return updatedRequest;
    }

    private Map<String, String> resolveConfiguredHeaders(JsonNode config, EvaluationContext context) {
        if (config == null || config.isMissingNode()) {
            return Map.of();
        }

        Map<String, String> headers = new HashMap<>();
        config.fields().forEachRemaining(entry -> 
            headers.put(entry.getKey(), context.resolve(entry.getValue().asText(), String.class))
        );
        return headers;
    }

    private OrchestratorRequest ensureDefaultHeader(OrchestratorRequest request, String name, String value) {
        if (!request.hasHeaderIgnoreCase(name)) {
            return request.withHeader(name, value);
        }
        return request;
    }
}
