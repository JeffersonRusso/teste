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
import java.util.stream.Collectors;

/**
 * Mestre da montagem de instâncias de Task.
 * Responsável por criar o núcleo técnico e envolver com a pilha de interceptores (Features).
 * Java 21: Utiliza String Templates e SequencedCollections para clareza e imutabilidade.
 */
@Slf4j
@Component
public class TaskBuilder {

    private final Map<String, TaskProvider> providers;
    private final Map<String, TaskInterceptor> interceptorsMap;
    private final FeatureResolver featureResolver;
    private final FeatureConfigFactory configFactory;

    public TaskBuilder(final List<TaskProvider> providerList,
                       final List<TaskInterceptor> interceptorList,
                       final FeatureResolver featureResolver,
                       final FeatureConfigFactory configFactory) {
        
        // Java 21: toUnmodifiableMap para garantir imutabilidade do catálogo de providers
        this.providers = providerList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        p -> p.getType().toUpperCase(),
                        p -> p
                ));

        // Mapeamos os interceptores pelo tipo definido na anotação @Component, garantindo desacoplamento do nome do Bean
        this.interceptorsMap = interceptorList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        i -> i.getClass().getAnnotation(Component.class).value().toUpperCase(),
                        i -> i
                ));

        this.featureResolver = featureResolver;
        this.configFactory = configFactory;
    }

    /**
     * Constrói uma Task completa (Core + Interceptors) a partir de sua definição.
     */
    public Task build(final TaskDefinition def) {
        // 1. Cria o núcleo técnico (HttpTask, GroovyTask, etc.)
        Task coreTask = createCoreTask(def);

        // 2. Monta a "Cebola" de funcionalidades (Features)
        return buildInterceptorStack(coreTask, def);
    }

    private Task createCoreTask(final TaskDefinition def) {
        String type = def.getType().toUpperCase();
        TaskProvider provider = providers.get(type);
        
        if (provider == null) {
            // Java 21: String Template para mensagens de erro ricas
            throw new TaskConfigurationException(STR."Sem provider registrado para o tipo de task: \{type}");
        }
        
        return provider.create(def);
    }

    private Task buildInterceptorStack(final Task coreTask, final TaskDefinition def) {
        // Java 21: toList() para obter uma lista imutável e eficiente de passos
        List<InterceptorStep> steps = def.getAllFeaturesOrdered().stream()
                .map(this::resolveStep)
                .filter(Objects::nonNull)
                .toList(); 

        if (steps.isEmpty()) {
            return coreTask;
        }
        
        // Retornamos a Task decorada com a pilha de interceptores
        return new InterceptorStack(coreTask, steps, def);
    }

    private InterceptorStep resolveStep(final FeatureDefinition feat) {
        // Resolve heranças ou referências de features globais
        final FeatureDefinition resolvedFeat = featureResolver.resolve(feat);
        
        TaskInterceptor interceptor = interceptorsMap.get(resolvedFeat.type().toUpperCase());
        
        if (interceptor == null) {
            log.warn("Feature ignorada (Interceptor não encontrado): {}", resolvedFeat.type());
            return null;
        }

        // O configFactory agora entrega o Record imutável já parseado e validado
        Object config = configFactory.parse(interceptor, resolvedFeat.config());
        return new InterceptorStep(interceptor, config);
    }
}
