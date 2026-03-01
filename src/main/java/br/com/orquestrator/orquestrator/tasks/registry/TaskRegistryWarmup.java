package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineVersionRepository;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TaskRegistryWarmup: Aquece o cache de pipelines e instâncias de tasks no startup.
 * Evita latência no primeiro request (Cold Start).
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
        
        // 1. Busca todos os tipos de operação que possuem versões ativas
        List<String> activeOperations = versionRepository.findAllActiveOperations();

        for (String operation : activeOperations) {
            try {
                log.debug("Aquecendo pipeline para operação: {}", operation);
                
                // 2. Cria um contexto fake apenas para disparar a compilação e cache
                ExecutionContext dummyContext = new ExecutionContext(
                    "WARMUP", 
                    operation, 
                    Map.of()
                );

                // 3. Chama o service. O @Cacheable fará o trabalho de guardar o resultado.
                pipelineService.create(dummyContext);
                
                log.info("Pipeline [{}] aquecido com sucesso.", operation);
            } catch (Exception e) {
                log.error("Falha ao aquecer pipeline [{}]: {}", operation, e.getMessage());
            }
        }
        
        log.info("Warm-up concluído. Sistema pronto para receber tráfego.");
    }
}
