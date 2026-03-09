package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

/**
 * HttpTaskProvider: Fábrica para tarefas HTTP.
 * Resolve a configuração estática no startup e prepara a task.
 */
@Component
@RequiredArgsConstructor
public class HttpTaskProvider implements TaskProvider {

    private final RestClient.Builder restClientBuilder;
    private final ExpressionEngine expressionEngine;
    private final TaskBindingResolver bindingResolver;
    private final ObjectMapper objectMapper;

    @Override public String getType() { return "HTTP"; }
    
    @Override public Optional<Class<?>> getConfigClass() { 
        return Optional.of(HttpTaskConfiguration.class); 
    }

    @Override
    public Task create(TaskDefinition definition) {
        // Resolve a configuração (estática ou com placeholders) no startup
        HttpTaskConfiguration config = bindingResolver.resolve(definition.config(), Map.of(), HttpTaskConfiguration.class);
        
        return new HttpTask(
            restClientBuilder.build(), 
            expressionEngine, 
            objectMapper, 
            config, 
            definition.getRequiredFields()
        );
    }
}
