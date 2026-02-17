package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Executor HTTP: Converte JSON para Map Java na fronteira.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private RestClient client;

    @PostConstruct
    public void init() {
        this.client = restClientBuilder.build();
    }

    public Object execute(OrchestratorRequest request, String nodeId, ExecutionContext context) {
        context.track(nodeId, "http.url", request.uri().toString());

        try {
            return client.method(HttpMethod.valueOf(request.method()))
                    .uri(request.uri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> {
                        if (request.headers() != null) request.headers().forEach(h::add);
                    })
                    .body(request.body() != null ? request.body() : "")
                    .retrieve()
                    .onStatus(status -> true, (req, res) -> {
                        context.setStatus(nodeId, res.getStatusCode().value());
                    })
                    .body(Object.class); // Converte para Map/List Java nativo

        } catch (Exception e) {
            throw handleHttpError(e, nodeId, request);
        }
    }

    private RuntimeException handleHttpError(Exception e, String nodeId, OrchestratorRequest request) {
        log.error("   [HttpExecutor] Falha na task {}: {}", nodeId, e.getMessage());
        return new TaskExecutionException(STR."Erro na requisição HTTP: \{e.getMessage()}", e)
                .withNodeId(nodeId)
                .addMetadata("url", request.uri().toString());
    }
}
