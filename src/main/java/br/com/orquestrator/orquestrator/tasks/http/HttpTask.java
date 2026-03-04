package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class HttpTask implements Task {

    private final RestClient restClient;
    private final HttpTaskConfiguration config;

    @Override
    public TaskResult execute() {
        var request = restClient.method(HttpMethod.valueOf(config.method().toUpperCase()))
                .uri(config.url())
                .headers(h -> { if (config.headers() != null) config.headers().forEach(h::add); });

        // Proteção contra body nulo: O RestClient falha se passarmos null para o body()
        if (config.body() != null) {
            request.body(config.body());
        }

        return request.exchange((req, res) -> {
            Object body = res.getStatusCode().is2xxSuccessful() ? res.bodyTo(Object.class) : null;
            return TaskResult.success(body, java.util.Map.of("status", res.getStatusCode().value()));
        });
    }
}
