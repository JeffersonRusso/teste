package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.domain.vo.NodeId;
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
 * Java 21: Refatorado para execução síncrona via Bootstrapper.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final TaskCatalogProvider taskProvider;
    private final TaskRegistry taskRegistry;

    /**
     * Executa o aquecimento inicial do catálogo. Chamado pelo OrchestratorBootstrapper.
     */
    public void warmup() {
        log.info("Aquecendo catálogo de instâncias de Task...");
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
                log.error(STR."FALHA ao carregar task [\{def.getNodeId()}]: \{e.getMessage()}");
                errorCount++;
            }
        }

        if (!newCache.isEmpty()) {
            taskRegistry.refreshRegistry(newCache);
            log.info(STR."Catálogo de instâncias atualizado em \{System.currentTimeMillis() - start}ms. Tasks: \{newCache.size()}, Erros: \{errorCount}");
        } else {
            log.warn("Nenhuma task válida encontrada. Cache não atualizado.");
        }
    }
}
