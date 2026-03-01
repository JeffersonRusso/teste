package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskChainCompiler: Responsável por compilar a cadeia de decoradores em torno de uma task core.
 */
@Slf4j
@Component
public class TaskChainCompiler {

    private final Map<String, DecoratorFactory<?>> decoratorFactories;
    private final ObjectMapper objectMapper;

    public TaskChainCompiler(List<DecoratorFactory<?>> factories, ObjectMapper objectMapper) {
        this.decoratorFactories = factories.stream()
                .collect(Collectors.toUnmodifiableMap(f -> f.getType().toUpperCase(), Function.identity()));
        this.objectMapper = objectMapper;
    }

    public Task compile(Task core, TaskDefinition def) {
        List<FeatureDefinition> features = def.features();
        if (features.isEmpty()) return core;

        List<TaskDecorator> decorators = features.stream()
                .map(f -> createDecorator(f, def.nodeId().value()))
                .filter(Objects::nonNull)
                .toList();

        if (decorators.isEmpty()) return core;

        // Monta a cadeia recursiva (Boneca Russa)
        Task current = core;
        for (int i = decorators.size() - 1; i >= 0; i--) {
            final TaskDecorator decorator = decorators.get(i);
            final Task next = current;
            current = () -> decorator.apply(next::execute);
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private TaskDecorator createDecorator(FeatureDefinition feature, String nodeId) {
        try {
            DecoratorFactory<Object> factory = (DecoratorFactory<Object>) decoratorFactories.get(feature.type().toUpperCase());
            if (factory == null) return null;
            Object config = objectMapper.convertValue(feature.config(), factory.getConfigClass());
            return factory.create(config, nodeId);
        } catch (Exception e) {
            log.error("Falha ao criar decorador {} no nó {}", feature.type(), nodeId, e);
            return null;
        }
    }
}
