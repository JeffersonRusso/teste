package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import br.com.orquestrator.orquestrator.core.engine.runtime.BackgroundExecutionEngine;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GlobalTaskScheduler: Responsável apenas pelo AGENDAMENTO de tarefas.
 * Delega a execução para o BackgroundExecutionEngine.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final PipelineNodeRepository nodeRepository;
    private final TaskScheduler taskScheduler;
    private final BackgroundExecutionEngine backgroundEngine;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Escaneando tasks globais para agendamento...");
        List<PipelineNodeEntity> allNodes = nodeRepository.findAll();

        for (var node : allNodes) {
            var config = node.getConfiguration();
            if (config != null && Boolean.TRUE.equals(config.getGlobal()) && config.getCron() != null) {
                schedule(node.toDomain(), config.getCron());
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
