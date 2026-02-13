package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.registry.factory.parser.FeatureConfigFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStack;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStep;
import br.com.orquestrator.orquestrator.tasks.interceptor.TaskInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskBuilder {

    private final Map<String, TaskProvider> providers;
    private final Map<String, TaskInterceptor> interceptorsMap;
    private final FeatureResolver featureResolver;
    private final FeatureConfigFactory configFactory;

    public TaskBuilder(final List<TaskProvider> providerList,
                       final Map<String, TaskInterceptor> interceptorsMap,
                       final FeatureResolver featureResolver,
                       final FeatureConfigFactory configFactory) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        p -> p.getType().toUpperCase(),
                        p -> p
                ));
        this.interceptorsMap = interceptorsMap;
        this.featureResolver = featureResolver;
        this.configFactory = configFactory;
    }

    public Task build(final TaskDefinition def) {
        Task coreTask = createCoreTask(def);
        return buildInterceptorStack(coreTask, def);
    }

    private Task createCoreTask(final TaskDefinition def) {
        String type = def.getType().toUpperCase();
        return Optional.ofNullable(providers.get(type))
                .map(provider -> provider.create(def))
                .orElseThrow(() -> new TaskConfigurationException("Tipo de task desconhecido ou sem provider registrado: " + type));
    }

    private Task buildInterceptorStack(final Task coreTask, final TaskDefinition def) {
        List<InterceptorStep> steps = def.getAllFeaturesOrdered().stream()
                .map(this::resolveStep)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (steps.isEmpty()) {
            return coreTask;
        }
        
        return new InterceptorStack(coreTask, steps, def);
    }

    private InterceptorStep resolveStep(final FeatureDefinition feat) {
        final FeatureDefinition resolvedFeat = featureResolver.resolve(feat);
        
        return Optional.ofNullable(interceptorsMap.get(resolvedFeat.type()))
                .map(interceptor -> {
                    Object config = configFactory.parse(interceptor, resolvedFeat.config());
                    return new InterceptorStep(interceptor, config);
                })
                .orElseGet(() -> {
                    log.warn("Feature ignorada (Interceptor não encontrado): {}", resolvedFeat.type());
                    return null;
                });
    }
}
