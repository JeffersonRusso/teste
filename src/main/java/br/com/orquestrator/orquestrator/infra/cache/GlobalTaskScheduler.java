package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.core.engine.executor.BackgroundExecutionEngine;
import br.com.orquestrator.orquestrator.core.ports.output.TaskRepository;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GlobalTaskScheduler: Responsável pelo AGENDAMENTO de tarefas globais.
 * Corrigido para usar o Domínio Rico do TaskDefinition.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final TaskRepository taskRepository;
    private final TaskScheduler taskScheduler;
    private final BackgroundExecutionEngine backgroundEngine;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Escaneando tasks globais para agendamento...");
        List<TaskDefinition> allTasks = taskRepository.findAll();

        for (TaskDefinition task : allTasks) {
            // Agora usamos o TaskBehavior rico definido no domínio
            var behavior = task.behavior();
            
            if (behavior.isGlobal() && behavior.cron() != null && !behavior.cron().isBlank()) {
                schedule(task, behavior.cron());
            }
        }
    }

    private void schedule(TaskDefinition def, String cron) {
        try {
            taskScheduler.schedule(() -> backgroundEngine.execute(def), new CronTrigger(cron));
            log.info("Task global [{}] agendada via Cron: {}", def.nodeId().value(), cron);
        } catch (Exception e) {
            log.error("Falha ao agendar task global {}: {}", def.nodeId().value(), e.getMessage());
        }
    }
}
