package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HttpTask: Executa chamadas HTTP com Extração Cirúrgica de dados.
 * Otimizada para alto volume e compatível com Transfer-Encoding: chunked.
 */
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
        
        Map<String, Object> rawInputs = new HashMap<>();
        context.inputs().forEach((k, v) -> rawInputs.put(k, v.raw()));

        String resolvedUrl = expressionEngine.compile(config.url()).evaluate(rawInputs).raw().toString();
        URI uri = URI.create(resolvedUrl);

        var requestSpec = restClient.method(HttpMethod.valueOf(config.method().toUpperCase()))
                .uri(uri)
                .headers(h -> { 
                    if (config.headers() != null) {
                        config.headers().forEach((k, v) -> {
                            h.add(k, expressionEngine.compile(v).evaluate(rawInputs).raw().toString());
                        });
                    }
                });

        if (config.body() != null) {
            Object bodyValue = expressionEngine.compile(config.body().toString()).evaluate(rawInputs).raw();
            if (bodyValue != null) requestSpec.body(bodyValue);
        }

        return requestSpec.exchange((req, res) -> {
            if (!res.getStatusCode().is2xxSuccessful()) {
                log.error("Falha na chamada HTTP para {}: {}", resolvedUrl, res.getStatusCode());
                return TaskResult.error(res.getStatusCode().value(), "Erro HTTP: " + res.getStatusCode());
            }

            try (InputStream is = res.getBody()) {

                try (JsonParser parser = objectMapper.getFactory().createParser(is)) {
                    // Verifica se o stream está vazio (comum em 204 No Content ou chunked vazio)
                    JsonToken firstToken = parser.nextToken();
                    if (firstToken == null) {
                        return TaskResult.success(DataValue.EMPTY);
                    }

                    // EXTRAÇÃO CIRÚRGICA: Lê apenas o necessário do stream
                    JsonNode resultNode = extractSelective(parser, context.requiredFields());
                    return TaskResult.success(DataValueFactory.fromJsonNode(resultNode, null), Map.of("status", res.getStatusCode().value()));
                }
            } catch (IOException e) {
                log.error("Erro ao processar stream JSON da URL: {}", resolvedUrl, e);
                throw new RuntimeException("Falha no streaming de dados HTTP", e);
            }
        }, false);
    }

    private JsonNode extractSelective(JsonParser parser, Set<String> requiredFields) throws IOException {
        // Se o parser já consumiu o primeiro token no check de vazio, precisamos lidar com isso
        JsonToken currentToken = parser.currentToken();

        // Se pedir o objeto todo ('.') ou não especificar campos, lê a árvore completa
        if (requiredFields == null || requiredFields.isEmpty() || requiredFields.contains(".")) {
            return objectMapper.readTree(parser);
        }

        // Se não for o início de um objeto, lê o valor atual e encerra
        if (currentToken != JsonToken.START_OBJECT) {
            return objectMapper.readTree(parser);
        }

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
