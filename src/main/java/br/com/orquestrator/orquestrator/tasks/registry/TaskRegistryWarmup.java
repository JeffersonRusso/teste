package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsável pelo aquecimento do cache de instâncias de Task.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final TaskCatalogProvider taskProvider;
    private final TaskRegistry taskRegistry;

    public void warmup() {
        log.info("Aquecendo catálogo de instâncias de Task...");
        refreshTasks();
    }

    @Scheduled(fixedRateString = "${app.tasks.refresh-rate:300000}")
    public void onSchedule() {
        refreshTasks();
    }

    private void refreshTasks() {
        List<TaskDefinition> definitions;
        try {
            definitions = taskProvider.findAllActive();
        } catch (Exception e) {
            log.error("Falha ao buscar tasks no banco.", e);
            return;
        }

        Map<String, Task> newCache = new HashMap<>();
        for (TaskDefinition def : definitions) {
            try {
                Task task = taskRegistry.getTask(def);
                newCache.put(STR."\{def.getNodeId().value()}:\{def.getVersion()}", task);
            } catch (Exception e) {
                log.error(STR."FALHA ao carregar task [\{def.getNodeId()}]: \{e.getMessage()}");
            }
        }

        if (!newCache.isEmpty()) {
            taskRegistry.refresh(newCache);
        }
    }
}
