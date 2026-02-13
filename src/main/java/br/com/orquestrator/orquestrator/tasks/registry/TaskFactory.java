package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.ConfigurableTask;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TaskFactory implements TaskRegistry {

    private final TaskBuilder taskBuilder;
    
    // Volatile garante visibilidade imediata da troca de referência para todas as threads
    private volatile Map<NodeId, Task> taskCache = Collections.emptyMap();

    public TaskFactory(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;
    }

    @Override
    public Task getTask(TaskDefinition def) {
        // Leitura rápida da referência volátil
        Map<NodeId, Task> currentCache = this.taskCache;
        
        Task task = currentCache.get(def.getNodeId());
        if (task != null) {
            return task;
        }
        
        // Fallback: Criação on-the-fly
        // Loga aviso pois isso pode indicar problema no warmup ou degradação de performance
        log.warn("Task [{}] não encontrada no cache. Criando instância sob demanda. Verifique o TaskRegistryWarmup.", def.getNodeId());
        return createNewTask(def);
    }
    
    @Override
    public Task createNewTask(TaskDefinition def) {
        Task task = taskBuilder.build(def);
        
        if (task instanceof ConfigurableTask configurable) {
            configurable.validateConfig();
        }
        
        return task;
    }

    @Override
    public void refreshRegistry(Map<NodeId, Task> newTasks) {
        // Troca atômica da referência do mapa.
        this.taskCache = new ConcurrentHashMap<>(newTasks);
        log.info("Cache de tasks atualizado com {} entradas.", newTasks.size());
    }
    
    @Override
    public void clearRegistry() {
        this.taskCache = Collections.emptyMap();
        log.info("Cache de tasks limpo.");
    }
}
