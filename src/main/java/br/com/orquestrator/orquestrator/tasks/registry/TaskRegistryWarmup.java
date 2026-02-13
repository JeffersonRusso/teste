package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final TaskCatalogProvider taskProvider;
    private final TaskRegistry taskRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Iniciando aquecimento (warmup) do catálogo de tasks...");
        refreshTasks();
    }

    @Scheduled(fixedRateString = "${app.tasks.refresh-rate:300000}")
    public void onSchedule() {
        log.debug("Verificando atualizações de tasks...");
        refreshTasks();
    }

    private void refreshTasks() {
        long start = System.currentTimeMillis();
        List<TaskDefinition> definitions;
        
        try {
            definitions = taskProvider.findAllActive();
        } catch (Exception e) {
            log.error("Falha ao buscar tasks no banco. Mantendo cache antigo.", e);
            return;
        }

        Map<NodeId, Task> newCache = new HashMap<>();
        int errorCount = 0;

        for (TaskDefinition def : definitions) {
            try {
                Task task = taskRegistry.createNewTask(def);
                newCache.put(def.getNodeId(), task);
            } catch (Exception e) {
                log.error("FALHA ao carregar task [{}]: {}", def.getNodeId(), e.getMessage());
                errorCount++;
            }
        }

        if (!newCache.isEmpty()) {
            taskRegistry.refreshRegistry(newCache);
            log.info("Catálogo atualizado em {}ms. Tasks: {}, Erros: {}", 
                    System.currentTimeMillis() - start, newCache.size(), errorCount);
        } else {
            log.warn("Nenhuma task válida encontrada. Cache não atualizado.");
        }
    }
}
