package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final java.util.List<CloseableHttpClient> apacheHttpClientPool;
    private final ObjectMapper objectMapper;
    private final AtomicInteger counter = new AtomicInteger(0);

    public TaskResult execute(OrchestratorRequest request, String nodeId) {
        try {
            CloseableHttpClient client = apacheHttpClientPool.get(counter.getAndIncrement() & 15);
            BasicClassicHttpRequest httpRequest = new BasicClassicHttpRequest(request.method(), request.uri());

            if (request.body() != null && !request.body().isEmpty()) {
                httpRequest.setEntity(new StringEntity(request.body(), ContentType.APPLICATION_JSON));
            }
            if (request.headers() != null) {
                request.headers().forEach(httpRequest::addHeader);
            }

            return client.execute(httpRequest, response -> {
                Object body = null;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // STREAMING DIRETO: Mata a alocação de byte[] detectada no JFR
                    try (InputStream is = entity.getContent()) {
                        body = objectMapper.readValue(is, Object.class);
                    }
                }
                return new TaskResult(body, response.getCode(), Map.of());
            });
        } catch (Exception e) {
            throw new TaskExecutionException("Erro HTTP: " + e.getMessage(), e).withNodeId(nodeId);
        }
    }
}
