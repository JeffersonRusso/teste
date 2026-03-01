package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TaskRegistry: Gerencia as instâncias puras das tasks.
 * Focado apenas na criação e cache das implementações core (Http, S3, etc).
 */
@Slf4j
@Component
public class TaskRegistry {

    private final Map<String, TaskProvider> providers;
    private final Map<String, Task> cache = new ConcurrentHashMap<>(1024);

    public TaskRegistry(List<TaskProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(p -> p.getType().toUpperCase(), Function.identity()));
    }

    public Task getTask(TaskDefinition def) {
        String key = STR."\{def.type().toUpperCase()}:\{def.nodeId().value()}";
        return cache.computeIfAbsent(key, _ -> buildTask(def));
    }

    private Task buildTask(TaskDefinition def) {
        TaskProvider provider = providers.get(def.type().toUpperCase());
        if (provider == null) {
            throw new RuntimeException(STR."Provedor não encontrado para o tipo: \{def.type()}");
        }
        return provider.create(def);
    }
}
