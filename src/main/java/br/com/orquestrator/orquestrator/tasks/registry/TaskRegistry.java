package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TaskRegistry {

    private final Map<String, TaskProvider> providerMap;
    private final Map<String, Task> instanceCache = new ConcurrentHashMap<>(1024);

    public TaskRegistry(List<TaskProvider> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toUnmodifiableMap(
                    p -> p.getType().toUpperCase(), 
                    Function.identity()
                ));
    }

    public Task getTask(TaskDefinition def) {
        return instanceCache.computeIfAbsent(def.nodeId().value(), id -> createInstance(def));
    }

    /** Retorna a classe de configuração para um tipo de task. */
    public Optional<Class<?>> getConfigClass(String type) {
        return Optional.ofNullable(providerMap.get(type.toUpperCase()))
                .flatMap(TaskProvider::getConfigClass);
    }

    private Task createInstance(TaskDefinition def) {
        String type = def.type().toUpperCase();
        TaskProvider provider = providerMap.get(type);
        if (provider == null) throw new PipelineException("Provedor não encontrado: " + type);
        return provider.create(def);
    }

    public void clear() { instanceCache.clear(); }
}
