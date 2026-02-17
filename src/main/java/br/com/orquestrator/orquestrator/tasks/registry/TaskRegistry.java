package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStack;
import br.com.orquestrator.orquestrator.tasks.interceptor.InterceptorStep;
import br.com.orquestrator.orquestrator.tasks.interceptor.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.registry.factory.parser.FeatureConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registro Central de Tasks: O único lugar que cria e guarda instâncias.
 */
@Slf4j
@Service
public class TaskRegistry {

    private final Map<String, TaskProvider> providers;
    private final Map<String, TaskInterceptor> interceptors;
    private final FeatureConfigFactory configFactory;
    
    private final Map<String, Task> cache = new ConcurrentHashMap<>();

    public TaskRegistry(List<TaskProvider> providerList, 
                        List<TaskInterceptor> interceptorList,
                        FeatureConfigFactory configFactory) {
        this.providers = providerList.stream().collect(Collectors.toMap(p -> p.getType().toUpperCase(), p -> p));
        this.interceptors = interceptorList.stream().collect(Collectors.toMap(i -> i.getClass().getSimpleName().replace("Interceptor", "").toUpperCase(), i -> i));
        this.configFactory = configFactory;
    }

    public Task getTask(TaskDefinition def) {
        String key = STR."\{def.getNodeId().value()}:\{def.getVersion()}";
        return cache.computeIfAbsent(key, _ -> buildTask(def));
    }

    private Task buildTask(TaskDefinition def) {
        // 1. Cria o Core
        TaskProvider provider = providers.get(def.getType().toUpperCase());
        if (provider == null) throw new RuntimeException(STR."Provider não encontrado: \{def.getType()}");
        Task core = provider.create(def);

        // 2. Envolve com Interceptores (Features)
        List<InterceptorStep> steps = def.getAllFeaturesOrdered().stream()
                .map(feat -> {
                    TaskInterceptor interceptor = interceptors.get(feat.type().toUpperCase());
                    if (interceptor == null) return null;
                    return new InterceptorStep(interceptor, configFactory.parse(interceptor, feat.config()));
                })
                .filter(Objects::nonNull)
                .toList();

        return steps.isEmpty() ? core : new InterceptorStack(core, steps, def);
    }

    public void refresh(Map<String, Task> newTasks) {
        cache.clear();
        cache.putAll(newTasks);
        log.info("Registro de tasks atualizado: {} instâncias.", cache.size());
    }
}
