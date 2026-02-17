package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * HttpExecutor: Executor técnico otimizado.
 * SRP: Focado apenas na execução da chamada usando o cliente compartilhado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final RestClient restClient;

    public TaskResult execute(OrchestratorRequest request, String nodeId) {
        try {
            ResponseEntity<Object> response = restClient.method(HttpMethod.valueOf(request.method()))
                    .uri(request.uri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> {
                        if (request.headers() != null) request.headers().forEach(h::add);
                    })
                    .body(request.bodyOrEmpty())
                    .retrieve()
                    .toEntity(Object.class);

            return new TaskResult(
                response.getBody(), 
                response.getStatusCode().value(), 
                Map.of("http.url", request.uri().toString())
            );

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
