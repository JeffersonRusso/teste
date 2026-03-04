package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineVersionRepository;
import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * TaskRegistryWarmup: Aquece o cache de pipelines no startup.
 * Usa apenas a visão de metadados (Privilégio Mínimo).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final PipelineVersionRepository versionRepository;
    private final PipelineService pipelineService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        log.info("Iniciando Warm-up de Pipelines...");
        List<String> activeOperations = versionRepository.findAllActiveOperations();

        for (String operation : activeOperations) {
            try {
                // Cria um objeto de metadados fake apenas para o warmup
                ContextMetadata warmupMetadata = new ContextMetadata() {
                    @Override public String getCorrelationId() { return "WARMUP"; }
                    @Override public String getOperationType() { return operation; }
                    @Override public Set<String> getTags() { return Set.of("default"); }
                };

                // O PipelineService agora aceita ContextMetadata
                pipelineService.create(warmupMetadata);

                log.info("Pipeline [{}] aquecido com sucesso.", operation);
            } catch (Exception e) {
                log.error("Falha ao aquecer pipeline [{}]: {}", operation, e.getMessage());
            }
        }
        log.info("Warm-up concluído.");
    }
}
