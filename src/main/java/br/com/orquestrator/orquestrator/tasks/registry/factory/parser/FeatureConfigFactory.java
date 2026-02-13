package br.com.orquestrator.orquestrator.tasks.registry.factory.parser;

import br.com.orquestrator.orquestrator.tasks.interceptor.TaskInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeatureConfigFactory {

    private final ObjectMapper objectMapper;

    public Object parse(TaskInterceptor interceptor, JsonNode json) {
        Class<?> configClass = interceptor.getConfigClass();
        
        if (configClass != null && json != null) {
            try {
                return objectMapper.treeToValue(json, configClass);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao parsear configuração da feature: " + interceptor.getClass().getSimpleName(), e);
            }
        }
        
        // Fallback: Retorna o JsonNode se não houver classe de configuração definida
        return json;
    }
}
