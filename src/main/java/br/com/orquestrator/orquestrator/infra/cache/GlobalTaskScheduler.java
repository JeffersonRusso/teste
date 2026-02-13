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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final TaskCatalogProvider taskProvider;
    private final TaskExecutor taskExecutor;
    private final DataBusFactory dataBusFactory;
    private final GlobalDataCache globalCache;
    private final TaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Inicializando tasks globais...");
        List<TaskDefinition> globalTasks = taskProvider.findAllActive().stream()
                .filter(TaskDefinition::isGlobal)
                .toList();

        for (TaskDefinition def : globalTasks) {
            scheduleTask(def);
        }
    }

    private void scheduleTask(TaskDefinition def) {
        Runnable taskRunner = () -> executeGlobalTask(def);

        taskRunner.run();

        if (def.getRefreshIntervalMs() > 0) {
            taskScheduler.scheduleAtFixedRate(taskRunner, Duration.ofMillis(def.getRefreshIntervalMs()));
            log.info("Task global [{}] agendada a cada {}ms", def.getNodeId(), def.getRefreshIntervalMs());
        }
    }

    private void executeGlobalTask(TaskDefinition def) {
        try {
            log.debug("Executando refresh da task global: {}", def.getNodeId());
            
            String correlationId = "GLOBAL-REFRESH-" + def.getNodeId();
            ExecutionTracker tracker = new ExecutionTracker();
            
            // CORREÇÃO: Removido o argumento globalCache
            ExecutionContext context = new ExecutionContext(correlationId, Collections.emptyMap(), tracker);
            
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
            log.debug("Task global [{}] atualizada com sucesso.", def.getNodeId());

        } catch (Exception e) {
            log.error("Falha ao atualizar task global [{}]: {}", def.getNodeId(), e.getMessage());
        }
    }
}
