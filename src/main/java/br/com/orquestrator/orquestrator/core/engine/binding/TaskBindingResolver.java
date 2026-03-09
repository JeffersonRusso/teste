package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.infra.cache.CacheFactory;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskBindingResolver: Resolve expressões dinâmicas dentro da configuração de uma Task.
 * Agora usa Caffeine para cachear os ObjectReaders do Jackson.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskBindingResolver {

    private final ExpressionEngine expressionEngine;
    private final ObjectMapper objectMapper;
    
    // Cache de leitores Jackson para evitar recriação custosa
    private final Cache<Class<?>, ObjectReader> readerCache = CacheFactory.createHotCache(128);

    public <T> T resolve(Map<String, Object> rawConfig, Map<String, DataValue> inputs, Class<T> targetClass) {
        if (rawConfig == null || rawConfig.isEmpty()) {
            return convert(Map.of(), targetClass);
        }

        try {
            Map<String, Object> resolvedMap = new HashMap<>();
            rawConfig.forEach((key, value) -> 
                resolvedMap.put(key, expressionEngine.compile(value).evaluate(inputs).raw())
            );

            return convert(resolvedMap, targetClass);
            
        } catch (Exception e) {
            throw new TaskConfigurationException("Falha ao resolver binding para " + targetClass.getSimpleName(), e);
        }
    }

    private <T> T convert(Map<String, Object> map, Class<T> targetClass) {
        try {
            ObjectReader reader = readerCache.get(targetClass, objectMapper::readerFor);
            JsonNode node = objectMapper.valueToTree(map);
            return reader.readValue(node);
        } catch (Exception e) {
            return objectMapper.convertValue(map, targetClass);
        }
    }
}
