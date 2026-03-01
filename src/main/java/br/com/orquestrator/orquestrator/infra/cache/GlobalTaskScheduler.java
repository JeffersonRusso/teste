package br.com.orquestrator.orquestrator.infra.cache;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GlobalTaskScheduler: Identifica e agenda tasks marcadas como globais no JSON.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalTaskScheduler {

    private final PipelineNodeRepository nodeRepository;
    private final TaskScheduler taskScheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Escaneando tasks globais para agendamento...");
        List<PipelineNodeEntity> allNodes = nodeRepository.findAll();

        for (var node : allNodes) {
            var config = node.getConfiguration();
            if (config != null && Boolean.TRUE.equals(config.getGlobal()) && config.getCron() != null) {
                schedule(node, config.getCron());
            }
        }
    }

    private void schedule(PipelineNodeEntity node, String cron) {
        try {
            taskScheduler.schedule(() -> executeTask(node), new CronTrigger(cron));
            log.info("Task global [{}] agendada via Cron: {}", node.getName(), cron);
        } catch (Exception e) {
            log.error("Falha ao agendar task global {}: {}", node.getName(), e.getMessage());
        }
    }

    private void executeTask(PipelineNodeEntity node) {
        log.debug("Executando refresh da task global: {}", node.getName());
        // Lógica de execução...
    }
}
