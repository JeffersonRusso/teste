package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HttpTaskProvider implements TaskProvider {

    private final RestClient.Builder restClientBuilder;
    private final ExpressionEngine expressionEngine;
    private final ObjectMapper objectMapper;

    @Override public String getType() { return "HTTP"; }
    
    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(HttpTaskConfiguration.class); 
    }

    @Override
    public Task create(TaskDefinition definition) {
        // Passamos o ObjectMapper para a task usar o pool de buffers e streaming
        return new HttpTask(restClientBuilder.build(), expressionEngine, objectMapper);
    }
}
