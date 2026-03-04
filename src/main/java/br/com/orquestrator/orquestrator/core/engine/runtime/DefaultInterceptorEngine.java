package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultInterceptorEngine implements InterceptorEngine {

    private final Map<String, DecoratorFactory<?>> factories;
    private final ObjectMapper objectMapper;

    public DefaultInterceptorEngine(List<DecoratorFactory<?>> factoryList, ObjectMapper objectMapper) {
        this.factories = factoryList.stream()
                .collect(Collectors.toUnmodifiableMap(f -> f.getType().toUpperCase(), Function.identity()));
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TaskDecorator> resolveInterceptors(List<FeatureDefinition> features, String nodeId) {
        if (features == null) return List.of();
        
        return features.stream()
                .map(f -> createDecorator(f, nodeId))
                .filter(Objects::nonNull)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private TaskDecorator createDecorator(FeatureDefinition feature, String nodeId) {
        try {
            DecoratorFactory<Object> factory = (DecoratorFactory<Object>) factories.get(feature.type().toUpperCase());
            if (factory == null) {
                log.warn("Nenhuma fábrica encontrada para o interceptor: {}", feature.type());
                return null;
            }
            Object config = objectMapper.convertValue(feature.config(), factory.getConfigClass());
            return factory.create(config, nodeId);
        } catch (Exception e) {
            log.error("Falha ao instanciar interceptor {} no nó {}: {}", feature.type(), nodeId, e.getMessage());
            return null;
        }
    }
}
