package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RequiredArgsConstructor
public class HttpTask implements Task, Configurable<HttpTaskConfiguration> {

    private final RestClient restClient;
    private final ExpressionEngine expressionEngine;

    @Override
    public Class<HttpTaskConfiguration> getConfigClass() {
        return HttpTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        HttpTaskConfiguration config = context.getConfig();
        Map<String, Object> inputs = context.inputs();

        String resolvedUrl = expressionEngine.evaluate(config.url(), inputs).raw().toString();

        var request = restClient.method(HttpMethod.valueOf(config.method().toUpperCase()))
                .uri(resolvedUrl)
                .headers(h -> { 
                    if (config.headers() != null) {
                        config.headers().forEach((k, v) -> {
                            h.add(k, expressionEngine.evaluate(v, inputs).raw().toString());
                        });
                    }
                });

        if (config.body() != null) {
            request.body(expressionEngine.evaluate(config.body(), inputs).raw());
        }

        // exchange() com exchangeFunction é mais performático que bodyToEntity()
        return request.exchange((req, res) -> {
            // Lê o corpo imediatamente para liberar o buffer de rede
            Object body = null;
            if (res.getStatusCode().is2xxSuccessful()) {
                body = res.bodyTo(Object.class);
            } else {
                // Drena o corpo de erro para evitar o transferTo() no close()
                res.bodyTo(String.class);
            }
            return TaskResult.success(DataValue.of(body), Map.of("status", res.getStatusCode().value()));
        }, false); // false = não drena automaticamente, nós já drenamos
    }
}
