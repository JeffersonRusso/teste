package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpExecutor {

    private final RestClient.Builder restClientBuilder;
    private final HttpResponseProcessor responseProcessor;
    private final HttpErrorHandler errorHandler;
    
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
            RestClient.RequestBodySpec spec = client.method(HttpMethod.valueOf(method))
                    .uri(request.uri())
                    .contentType(MediaType.APPLICATION_JSON);

            if (request.headers() != null) {
                request.headers().forEach(spec::header);
            }

            if (request.body() != null) {
                spec.body(request.body());
            }

            spec.exchange((req, res) -> {
                data.addMetadata(TaskMetadataHelper.STATUS, res.getStatusCode().value());
                responseProcessor.process(res, definition, data);
                return null;
            });

        } catch (Exception e) {
            errorHandler.handle(e, nodeId, url, method);
        }
    }
}
