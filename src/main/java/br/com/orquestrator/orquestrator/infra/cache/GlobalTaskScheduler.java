package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.domain.ExecutionTracker;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.core.engine.DataBus;
import br.com.orquestrator.orquestrator.core.engine.DataBusFactory;
import br.com.orquestrator.orquestrator.core.engine.TaskExecutor;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Agendador de tarefas globais.
 * Java 21: Refatorado para execução síncrona via Bootstrapper.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final TaskCatalogProvider taskProvider;
    private final TaskExecutor taskExecutor;
    private final DataBusFactory dataBusFactory;
    private final GlobalDataCache globalCache;
    private final TaskScheduler taskScheduler;

    /**
     * Inicializa o agendamento de tasks globais. Chamado pelo OrchestratorBootstrapper.
     */
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
        Runnable taskRunner = () -> executeGlobalTask(def);

        // Execução inicial síncrona
        taskRunner.run();

        if (def.getRefreshIntervalMs() > 0) {
            taskScheduler.scheduleAtFixedRate(taskRunner, Duration.ofMillis(def.getRefreshIntervalMs()));
            log.info(STR."Task global [\{def.getNodeId()}] agendada a cada \{def.getRefreshIntervalMs()}ms");
        }
    }

    private void executeGlobalTask(TaskDefinition def) {
        try {
            log.debug(STR."Executando refresh da task global: \{def.getNodeId()}");
            
            String correlationId = STR."GLOBAL-REFRESH-\{def.getNodeId()}";
            String operationType = "GLOBAL_SYSTEM";
            ExecutionTracker tracker = new ExecutionTracker();
            
            ExecutionContext context = new ExecutionContext(
                    correlationId, 
                    operationType, 
                    tracker, 
                    Map.of()
            );
            
            long timeout = def.getTimeoutMs() > 0 ? def.getTimeoutMs() : 3600000;
            context.setDeadline(Instant.now().plusMillis(timeout));

            DataBus dataBus = dataBusFactory.create(context, Collections.singletonList(def));

            taskExecutor.execute(def, context, dataBus);

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
