package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskBindingResolver: Resolve expressões SpEL na configuração da task
 * usando diretamente o contexto global do request.
 */
@Component
@RequiredArgsConstructor
public class TaskBindingResolver {

    private final ExpressionService expressionService;
    private final ObjectMapper objectMapper;

    public <T> T resolve(Map<String, Object> rawConfig, Class<T> targetClass) {
        // Resolve recursivamente o mapa de configuração contra o contexto soberano
        Map<String, Object> resolvedMap = resolveRecursive(rawConfig);

        // Converte para o DTO tipado da task
        return objectMapper.convertValue(resolvedMap, targetClass);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveRecursive(Map<String, Object> source) {
        if (source == null) return Map.of();
        Map<String, Object> resolved = new HashMap<>();

        source.forEach((key, value) -> {
            if (value instanceof String str && (str.contains("#") || str.contains("${"))) {
                // Resolve usando o ExpressionService que já pega o EVAL_CONTEXT do escopo
                resolved.put(key, expressionService.resolve(str, Object.class));
            } else if (value instanceof Map) {
                resolved.put(key, resolveRecursive((Map<String, Object>) value));
            } else {
                resolved.put(key, value);
            }
        });

        return resolved;
    }
}
