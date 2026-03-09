package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.core.pipeline.PipelineCompiler;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final PipelineRepository pipelineRepository;
    private final PipelineCompiler pipelineCompiler;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        log.info("Iniciando Warm-up do Orquestrador...");
        
        log.info("Carregando Contratos de Dados...");
        // O ContractRegistry agora é auto-suficiente e carrega sob demanda.
        // Não precisamos mais de um método register explícito.

        log.info("Pré-compilando pipelines ativos...");
        pipelineRepository.findAllActiveOperationTypes().forEach(operationType -> {
            try {
                pipelineRepository.findActive(operationType).ifPresent(def -> 
                    pipelineCompiler.compile(def, Set.of("default"))
                );
            } catch (Exception e) {
                log.error("Falha ao aquecer pipeline [{}]: {}", operationType, e.getMessage());
            }
        });

        log.info("Warm-up concluído.");
    }
}
