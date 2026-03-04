package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskBindingResolver {

    private final ExpressionEngine expressionEngine;
    private final ObjectMapper objectMapper;

    public <T> T resolve(Map<String, Object> rawConfig, Class<T> targetClass) {
        if (rawConfig == null || rawConfig.isEmpty()) {
            return objectMapper.convertValue(Map.of(), targetClass);
        }

        try {
            // Resolve o mapa usando o motor unificado e o contexto do escopo
            Map<String, Object> resolvedMap = expressionEngine.resolveMap(rawConfig, ContextHolder.reader());
            return objectMapper.convertValue(resolvedMap, targetClass);
        } catch (Exception e) {
            throw new TaskConfigurationException("Falha ao resolver binding para a classe " + targetClass.getSimpleName(), e);
        }
    }
}
