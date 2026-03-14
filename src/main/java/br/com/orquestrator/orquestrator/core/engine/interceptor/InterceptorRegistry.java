package br.com.orquestrator.orquestrator.core.engine.interceptor;

import br.com.orquestrator.orquestrator.api.task.InterceptorProvider;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.domain.model.definition.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * InterceptorRegistry: Central de descoberta de middlewares.
 * Utiliza descoberta nativa via nomes de Bean do Spring.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterceptorRegistry {

    // Spring injeta um mapa onde a chave é o nome no @Component("NOME")
    private final Map<String, InterceptorProvider> providers;

    public Optional<TaskInterceptor> create(FeatureDefinition feature, TaskDefinition taskDef) {
        String type = feature.type().toUpperCase();
        InterceptorProvider provider = providers.get(type);
        return (provider != null) ? provider.create(feature, taskDef) : Optional.empty();
    }

    public Optional<TaskInterceptor> createByType(String type, TaskDefinition taskDef) {
        return create(new FeatureDefinition(type, Map.of()), taskDef);
    }

    public Collection<InterceptorProvider> getAllProviders() {
        return providers.values();
    }
}
