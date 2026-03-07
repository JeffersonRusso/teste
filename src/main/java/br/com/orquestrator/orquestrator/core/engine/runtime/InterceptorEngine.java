package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * InterceptorEngine: Fábrica de interceptores lineares.
 */
@Slf4j
@Component
public class InterceptorEngine {

    private final Map<String, DecoratorFactory<?>> factoryMap;
    private final ObjectMapper objectMapper;

    public InterceptorEngine(List<DecoratorFactory<?>> factories, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.factoryMap = factories.stream()
                .collect(Collectors.toUnmodifiableMap(
                    f -> f.getType().toUpperCase(), 
                    Function.identity(),
                    (existing, replacement) -> existing
                ));
    }

    public List<TaskInterceptor> resolveInterceptors(List<FeatureDefinition> features, String nodeId) {
        if (features == null || features.isEmpty()) return List.of();

        return features.stream()
                .map(feature -> createInterceptor(feature, nodeId))
                .filter(Objects::nonNull)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private TaskInterceptor createInterceptor(FeatureDefinition feature, String nodeId) {
        String type = feature.type().toUpperCase();
        DecoratorFactory<Object> factory = (DecoratorFactory<Object>) factoryMap.get(type);

        if (factory == null) {
            log.warn("Nenhuma fábrica encontrada para o interceptor: {} no nó {}", type, nodeId);
            return null;
        }

        try {
            Object config = objectMapper.convertValue(feature.config(), factory.getConfigClass());
            return factory.create(config, nodeId);
        } catch (Exception e) {
            log.error("Erro ao configurar interceptor [{}] no nó [{}]: {}", type, nodeId, e.getMessage());
            return null;
        }
    }
}
