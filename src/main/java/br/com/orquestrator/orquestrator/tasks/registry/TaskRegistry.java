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

@Slf4j
@Service
public class TaskRegistry {

    private final Map<String, TaskProvider> providers;
    private final TaskDecorator decorator;
    private final Map<String, Task> cache = new ConcurrentHashMap<>(1024);

    public TaskRegistry(List<TaskProvider> providerList, TaskDecorator decorator) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(p -> p.getType().toUpperCase(), p -> p));
        this.decorator = decorator;
    }

    public Task getTask(TaskDefinition def) {
        // Otimização: Concatenação simples é mais rápida que String Template em alta carga
        String key = def.getNodeId().value() + ":" + def.getVersion();
        
        // Otimização: get() antes do computeIfAbsent evita contenção de lock no mapa
        Task task = cache.get(key);
        if (task != null) return task;

        return cache.computeIfAbsent(key, _ -> buildTask(def));
    }

    private Task buildTask(TaskDefinition def) {
        TaskProvider provider = providers.get(def.getType().toUpperCase());
        if (provider == null) {
            throw new RuntimeException("Provider não encontrado: " + def.getType());
        }
        return decorator.decorate(provider.create(def), def);
    }

    public void refresh(Map<String, Task> newTasks) {
        cache.clear();
        cache.putAll(newTasks);
    }
}
