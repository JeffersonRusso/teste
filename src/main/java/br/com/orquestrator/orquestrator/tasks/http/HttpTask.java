package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;

/**
 * HttpTask: Agora 100% agnóstica de bibliotecas JSON.
 */
@Slf4j
@RequiredArgsConstructor
public final class HttpTask implements Task {

    private final RestClient restClient;
    private final DataFactory dataFactory;
    private final CompiledConfiguration<HttpTaskConfiguration> config;

    @Override
    public TaskResult execute(TaskExecutionContext context) {
        HttpTaskConfiguration resolved = config.resolve(context.getInputs());
        
        log.debug("Executando chamada HTTP: {} {}", resolved.method(), resolved.url());

        try {
            String response = restClient.method(HttpMethod.valueOf(resolved.method().toUpperCase()))
                    .uri(resolved.url())
                    .retrieve()
                    .body(String.class);

            // Converte a resposta (String) para a abstração DataNode via Factory
            DataNode result = dataFactory.parse(response);
            return TaskResult.success(result);

        } catch (Exception e) {
            log.error("Erro na HttpTask [{}]: {}", context.getTaskName(), e.getMessage());
            return TaskResult.error(500, "Falha na chamada HTTP: " + e.getMessage());
        }
    }
}
