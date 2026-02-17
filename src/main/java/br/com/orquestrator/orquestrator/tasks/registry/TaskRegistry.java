package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TaskRegistry: O único ponto de criação e cache de instâncias de Task.
 */
@Slf4j
@Service
public class TaskRegistry {

    private final Map<String, TaskProvider> providers;
    private final TaskDecorator decorator;
    private final Map<String, Task> cache = new ConcurrentHashMap<>();

    public TaskRegistry(List<TaskProvider> providerList, TaskDecorator decorator) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(p -> p.getType().toUpperCase(), p -> p));
        this.decorator = decorator;
    }

    public Task getTask(TaskDefinition def) {
        String key = STR."\{def.getNodeId().value()}:\{def.getVersion()}";
        return cache.computeIfAbsent(key, _ -> buildTask(def));
    }

    private Task buildTask(TaskDefinition def) {
        TaskProvider provider = providers.get(def.getType().toUpperCase());
        if (provider == null) {
            throw new RuntimeException(STR."Provider não encontrado para o tipo: \{def.getType()}");
        }
        
        // 1. Cria o núcleo técnico
        Task core = provider.create(def);

        // 2. Decora com interceptores (SOLID: SRP)
        return decorator.decorate(core, def);
    }

    public void refresh(Map<String, Task> newTasks) {
        cache.clear();
        cache.putAll(newTasks);
        log.info("Registro de tasks atualizado: {} instâncias.", cache.size());
    }
}
