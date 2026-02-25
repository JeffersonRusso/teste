package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final List<HttpClient> javaHttpClientPool;
    private final ObjectMapper objectMapper;
    private ObjectReader reader;
    private int poolSize;

    @PostConstruct
    public void init() {
        this.reader = objectMapper.readerFor(Object.class);
        this.poolSize = javaHttpClientPool.size();
    }

    public TaskResult execute(OrchestratorRequest request, String nodeId) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return doExecute(request);
            } catch (IOException e) {
                if (e.getMessage() != null && e.getMessage().contains("header parser received no bytes") && attempt < 3) {
                    continue;
                }
                throw new TaskExecutionException("Erro HTTP: " + e.getMessage(), e).withNodeId(nodeId);
            } catch (Exception e) {
                throw new TaskExecutionException("Erro HTTP: " + e.getMessage(), e).withNodeId(nodeId);
            }
        }
        return null;
    }

    private TaskResult doExecute(OrchestratorRequest request) throws Exception {
        // CORREÇÃO: Usa o tamanho real do pool para evitar ArrayIndexOutOfBoundsException
        HttpClient client = javaHttpClientPool.get(ThreadLocalRandom.current().nextInt(poolSize));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(request.uri())
                .timeout(Duration.ofMillis(request.timeoutMs() > 0 ? request.timeoutMs() : 10000))
                .method(request.method(), request.body() != null && !request.body().isEmpty() 
                        ? HttpRequest.BodyPublishers.ofString(request.body()) 
                        : HttpRequest.BodyPublishers.noBody());

        if (request.headers() != null) {
            request.headers().forEach(builder::header);
        }

        HttpResponse<InputStream> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream is = response.body()) {
            if (response.statusCode() == 204 || is == null) {
                return new TaskResult(null, response.statusCode(), Map.of());
            }

            try (JsonParser parser = objectMapper.getFactory().createParser(is)) {
                Object body = reader.readValue(parser);
                return new TaskResult(body, response.statusCode(), Map.of());
            }
        }
    }
}
