package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
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

@Slf4j
@RequiredArgsConstructor
public class HttpTask implements Task, Configurable<HttpTaskConfiguration> {

    private final RestClient restClient;
    private final ExpressionEngine expressionEngine;
    private final ObjectMapper objectMapper;

    @Override
    public Class<HttpTaskConfiguration> getConfigClass() {
        return HttpTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        HttpTaskConfiguration config = context.getConfig();
        Map<String, Object> inputs = context.inputs();

        // OTIMIZAÇÃO: Compila e avalia a URL
        String resolvedUrl = expressionEngine.compile(config.url()).evaluate(inputs).raw().toString();
        URI uri = URI.create(resolvedUrl);

        var requestSpec = restClient.method(HttpMethod.valueOf(config.method().toUpperCase()))
                .uri(uri)
                .headers(h -> { 
                    if (config.headers() != null) {
                        config.headers().forEach((k, v) -> {
                            h.add(k, expressionEngine.compile(v).evaluate(inputs).raw().toString());
                        });
                    }
                });

        if (config.body() != null) {
            Object bodyValue = expressionEngine.compile(config.body().toString()).evaluate(inputs).raw();
            if (bodyValue != null) requestSpec.body(bodyValue);
        }

        return requestSpec.exchange((req, res) -> {
            if (!res.getStatusCode().is2xxSuccessful()) {
                res.bodyTo(String.class);
                return TaskResult.success(new DataValue.Empty(), Map.of("status", res.getStatusCode().value()));
            }

            try (InputStream is = res.getBody();
                 JsonParser parser = objectMapper.getFactory().createParser(is)) {
                
                JsonNode resultNode = extractSelective(parser, context.requiredFields());
                return TaskResult.success(DataValue.of(resultNode), Map.of("status", res.getStatusCode().value()));
                
            } catch (IOException e) {
                log.error("Erro ao processar stream JSON da URL: {}", resolvedUrl, e);
                throw new RuntimeException("Falha no streaming de dados HTTP", e);
            }
        }, false);
    }

    private JsonNode extractSelective(JsonParser parser, Set<String> requiredFields) throws IOException {
        if (requiredFields.contains(".")) {
            return objectMapper.readTree(parser);
        }

        if (parser.nextToken() != JsonToken.START_OBJECT) {
            return objectMapper.readTree(parser);
        }

        ObjectNode result = objectMapper.createObjectNode();
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken();

            if (requiredFields.isEmpty() || requiredFields.contains(fieldName)) {
                result.set(fieldName, objectMapper.readTree(parser));
            } else {
                parser.skipChildren();
            }
        }
        
        return result;
    }
}
