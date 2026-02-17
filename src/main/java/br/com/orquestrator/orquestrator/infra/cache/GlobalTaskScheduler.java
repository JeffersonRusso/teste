package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.core.engine.TaskRunner;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Agendador de tarefas globais.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final TaskCatalogProvider taskProvider;
    private final TaskRunner taskRunner;
    private final GlobalDataCache globalCache;
    private final TaskScheduler taskScheduler;

    public void initialize() {
        log.info("Inicializando agendamento de tasks globais...");
        List<TaskDefinition> globalTasks = taskProvider.findAllActive().stream()
                .filter(TaskDefinition::isGlobal)
                .toList();

        for (TaskDefinition def : globalTasks) {
            scheduleTask(def);
        }
    }

    private void scheduleTask(TaskDefinition def) {
        Runnable runner = () -> executeGlobalTask(def);

        runner.run();

        if (def.getRefreshIntervalMs() > 0) {
            taskScheduler.scheduleAtFixedRate(runner, Duration.ofMillis(def.getRefreshIntervalMs()));
            log.info(STR."Task global [\{def.getNodeId()}] agendada a cada \{def.getRefreshIntervalMs()}ms");
        }
    }

    private void executeGlobalTask(TaskDefinition def) {
        try {
            log.debug(STR."Executando refresh da task global: \{def.getNodeId()}");
            
            String correlationId = STR."GLOBAL-REFRESH-\{def.getNodeId()}";
            String operationType = "GLOBAL_SYSTEM";
            
            ExecutionContext context = new ExecutionContext(
                    correlationId, 
                    operationType, 
                    new ExecutionTracker(), 
                    Map.of()
            );
            
            taskRunner.run(def, context);

            if (def.getProduces() != null) {
                for (DataSpec spec : def.getProduces()) {
                    String outputKey = spec.name();
                    Object value = context.get(outputKey);
                    if (value != null) {
                        globalCache.put(outputKey, value);
                    }
                }
            }
            log.debug(STR."Task global [\{def.getNodeId()}] atualizada com sucesso.");

        } catch (Exception e) {
            log.error(STR."Falha ao atualizar task global [\{def.getNodeId()}]: \{e.getMessage()}");
        }
    }
}
