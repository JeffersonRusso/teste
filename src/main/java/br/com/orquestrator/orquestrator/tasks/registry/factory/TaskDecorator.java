package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.InterceptorStack;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskDecorator: Otimizado para evitar alocações de lambdas no caminho crítico.
 */
@Slf4j
@Component
public class TaskDecorator {

    private final Map<String, InterceptorProvider<?>> interceptorProviders;
    private final ObjectMapper objectMapper;

    public TaskDecorator(List<InterceptorProvider<?>> providers, ObjectMapper objectMapper) {
        this.interceptorProviders = providers.stream()
                .collect(Collectors.toUnmodifiableMap(p -> p.featureType().toUpperCase(), Function.identity()));
        this.objectMapper = objectMapper;
    }

    public Task decorate(Task core, TaskDefinition def) {
        final String nodeId = def.getNodeId().value();
        List<FeatureDefinition> features = def.getAllFeaturesOrdered();
        
        if (features.isEmpty()) return core;

        List<TaskInterceptor> interceptors = new ArrayList<>(features.size());
        for (FeatureDefinition feature : features) {
            TaskInterceptor interceptor = createInterceptor(feature, nodeId);
            if (interceptor != null) {
                interceptors.add(interceptor);
            }
        }

        return interceptors.isEmpty() ? core : new InterceptorStack(core, interceptors, def);
    }

    @SuppressWarnings("unchecked")
    private TaskInterceptor createInterceptor(FeatureDefinition feature, String nodeId) {
        try {
            String type = feature.type().toUpperCase();
            InterceptorProvider<Object> provider = (InterceptorProvider<Object>) interceptorProviders.get(type);
            
            if (provider == null) {
                log.warn("Feature '{}' ignorada: Nenhum provedor encontrado.", feature.type());
                return null;
            }

            Object config = objectMapper.convertValue(feature.config(), provider.configClass());
            TaskInterceptor interceptor = provider.create(config, nodeId);

            // Telemetria via classe concreta para evitar alocação de lambdas por request
            return new TelemetryInterceptor(interceptor, type, nodeId);

        } catch (Exception e) {
            log.error("Falha ao criar interceptor para a feature '{}' no nó '{}'", feature.type(), nodeId, e);
            return null;
        }
    }

    /**
     * Interceptor de Telemetria: Classe estática é mais performática que lambdas aninhados.
     */
    @RequiredArgsConstructor
    private static class TelemetryInterceptor implements TaskInterceptor {
        private final TaskInterceptor inner;
        private final String name;
        private final String nodeId;

        @Override
        public TaskChain apply(TaskChain next) {
            final TaskChain chain = inner.apply(next);
            return context -> {
                long start = System.nanoTime();
                try {
                    return chain.proceed(context);
                } finally {
                    long duration = (System.nanoTime() - start) / 1_000_000; // Manual ms conversion
                    context.track(nodeId, "interceptor." + name + ".duration_ms", duration);
                }
            };
        }
    }
}
