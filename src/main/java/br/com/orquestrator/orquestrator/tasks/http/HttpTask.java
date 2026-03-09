package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.infra.el.CompiledExpression;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * HttpTask: Executa chamadas HTTP com alocação mínima de objetos.
 * Otimizada para 100k+ RPS:
 * 1. Expressões pré-compiladas no startup.
 * 2. Zero-copy de inputs (usa o mapa original).
 * 3. Extração cirúrgica via streaming.
 */
@Slf4j
public class HttpTask implements Task {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final HttpMethod method;
    private final CompiledExpression urlExpression;
    private final Map<String, CompiledExpression> headerExpressions;
    private final CompiledExpression bodyExpression;
    private final Set<String> requiredFields;

    public HttpTask(RestClient restClient, 
                    ExpressionEngine engine, 
                    ObjectMapper objectMapper, 
                    HttpTaskConfiguration config, 
                    Set<String> requiredFields) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.method = HttpMethod.valueOf(config.method().toUpperCase());
        this.urlExpression = engine.compile(config.url());
        this.bodyExpression = config.body() != null ? engine.compile(config.body().toString()) : null;
        this.requiredFields = requiredFields;
        
        // Pré-compila headers
        this.headerExpressions = new java.util.HashMap<>();
        if (config.headers() != null) {
            config.headers().forEach((k, v) -> this.headerExpressions.put(k, engine.compile(v)));
        }
    }

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        // 1. Resolve URL usando o mapa original (Zero-Copy)
        String resolvedUrl = urlExpression.evaluate(inputs).raw().toString();
        
        var requestSpec = restClient.method(method).uri(resolvedUrl);

        // 2. Resolve Headers
        if (!headerExpressions.isEmpty()) {
            requestSpec.headers(h -> headerExpressions.forEach((k, expr) -> 
                h.add(k, expr.evaluate(inputs).raw().toString())));
        }

        // 3. Resolve Body
        if (bodyExpression != null) {
            Object bodyValue = bodyExpression.evaluate(inputs).raw();
            if (bodyValue != null) requestSpec.body(bodyValue);
        }

        return requestSpec.exchange((req, res) -> {
            if (!res.getStatusCode().is2xxSuccessful()) {
                return TaskResult.error(res.getStatusCode().value(), "Erro HTTP: " + res.getStatusCode());
            }

            try (InputStream is = res.getBody()) {
                if (is == null) return TaskResult.success(DataValue.EMPTY);
                try (JsonParser parser = objectMapper.getFactory().createParser(is)) {
                    if (parser.nextToken() == null) return TaskResult.success(DataValue.EMPTY);

                    JsonNode resultNode = extractSelective(parser, requiredFields);
                    return TaskResult.success(DataValueFactory.fromJsonNode(resultNode, null), Map.of("status", res.getStatusCode().value()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Falha no streaming de dados HTTP", e);
            }
        }, false);
    }

    private JsonNode extractSelective(JsonParser parser, Set<String> requiredFields) throws IOException {
        if (requiredFields == null || requiredFields.isEmpty() || requiredFields.contains(".")) {
            return objectMapper.readTree(parser);
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) return objectMapper.readTree(parser);

        ObjectNode result = objectMapper.createObjectNode();
        int foundCount = 0;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken();
            if (requiredFields.contains(fieldName)) {
                result.set(fieldName, objectMapper.readTree(parser));
                foundCount++;
                if (foundCount >= requiredFields.size()) break; 
            } else {
                parser.skipChildren();
            }
        }
        return result;
    }
}
