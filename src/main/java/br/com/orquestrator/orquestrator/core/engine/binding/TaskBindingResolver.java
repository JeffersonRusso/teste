package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TaskBindingResolver: Resolve e converte configurações usando ObjectReaders pré-compilados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskBindingResolver {

    private final ExpressionEngine expressionEngine;
    private final ObjectMapper objectMapper;
    
    private final Map<Class<?>, ObjectReader> readerCache = new ConcurrentHashMap<>(128);

    public <T> T resolve(Map<String, Object> rawConfig, Class<T> targetClass) {
        if (rawConfig == null || rawConfig.isEmpty()) {
            return convert(Map.of(), targetClass);
        }

        try {
            Map<String, Object> resolvedMap = expressionEngine.resolveMap(rawConfig, ContextHolder.reader());
            return convert(resolvedMap, targetClass);
        } catch (Exception e) {
            throw new TaskConfigurationException("Falha ao resolver binding para a classe " + targetClass.getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(Map<String, Object> map, Class<T> targetClass) {
        try {
            ObjectReader reader = readerCache.computeIfAbsent(targetClass, objectMapper::readerFor);
            
            // Converte o Map para JsonNode (operação rápida em memória)
            JsonNode node = objectMapper.valueToTree(map);
            
            // Usa o reader pré-compilado com cast explícito para evitar ambiguidade
            return reader.readValue(node);
            
        } catch (Exception e) {
            log.error("Erro na conversão Jackson otimizada para {}: {}", targetClass.getSimpleName(), e.getMessage());
            return objectMapper.convertValue(map, targetClass);
        }
    }
}
