package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeoutException;

/**
 * Executor de requisições HTTP.
 * Centraliza a execução técnica e o tratamento de erros de infraestrutura.
 * Java 21: Utiliza switch expressions e Pattern Matching para tratamento de erros.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final RestClient.Builder restClientBuilder;
    private final HttpResponseProcessor responseProcessor;
    private RestClient client;

    @PostConstruct
    public void init() {
        this.client = restClientBuilder.build();
    }

    public void execute(OrchestratorRequest request, TaskDefinition definition, TaskData data) {
        String nodeId = definition.getNodeId().value();
        String method = request.method();
        String url = request.uri().toString();

        data.addMetadata("http.url", url);
        data.addMetadata("http.method", method);

        try {
            client.method(HttpMethod.valueOf(method))
                    .uri(request.uri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> {
                        if (request.headers() != null) {
                            request.headers().forEach(h::add);
                        }
                    })
                    .body(request.body() != null ? request.body() : "")
                    .exchange((req, res) -> {
                        data.addMetadata(TaskMetadataHelper.STATUS, res.getStatusCode().value());
                        responseProcessor.process(res, definition, data);
                        return null;
                    });

        } catch (Exception e) {
            handleHttpError(e, nodeId, url, method);
        }
    }

    private void handleHttpError(Exception e, String nodeId, String url, String method) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            throw new TaskExecutionException("Execução interrompida", e).withNodeId(nodeId);
        }
        
        // Java 21: Switch Expression com Pattern Matching para tradução de erros
        String friendlyMessage = switch (e) {
            case HttpTimeoutException _, TimeoutException _ -> "Timeout na comunicação";
            case ConnectException _, ClosedChannelException _ -> "Serviço indisponível";
            default -> "Erro na requisição HTTP";
        };

        log.error("   [HttpExecutor] Falha na task {}: {} - {} {}", nodeId, friendlyMessage, method, url);

        throw new TaskExecutionException(STR."\{friendlyMessage} (\{extractError(e)})", e)
                .withNodeId(nodeId)
                .addMetadata("url", url)
                .addMetadata("method", method);
    }

    private String extractError(Throwable e) {
        if (e == null) return "Unknown";
        return (e.getMessage() == null || e.getMessage().isBlank()) 
            ? e.getClass().getSimpleName() 
            : e.getMessage();
    }
}
