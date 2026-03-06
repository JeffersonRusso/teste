package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.DataContractRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineVersionRepository;
import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.model.DataContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final PipelineVersionRepository versionRepository;
    private final DataContractRepository contractRepository;
    private final ContractRegistry contractRegistry;
    private final PipelineService pipelineService;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        log.info("Iniciando Warm-up do Orquestrador...");
        
        // 1. Carrega Contratos de Dados
        loadContracts();

        // 2. Aquece Pipelines
        loadPipelines();
        
        log.info("Warm-up concluído.");
    }

    private void loadContracts() {
        log.info("Carregando Contratos de Dados...");
        contractRepository.findAll().forEach(entity -> {
            DataContract contract = new DataContract(
                entity.getContextKey(),
                entity.getDataType(),
                entity.getSemanticType(),
                entity.getFormatRule(),
                entity.getSchemaDefinition(),
                entity.getMinValue(),
                entity.getMaxValue(),
                entity.getIsRequired() != null ? entity.getIsRequired() : false,
                entity.getDescription()
            );
            contractRegistry.register(contract);
        });
    }

    private void loadPipelines() {
        List<String> activeOperations = versionRepository.findAllActiveOperations();
        for (String operation : activeOperations) {
            try {
                ContextMetadata warmupMetadata = new ContextMetadata() {
                    @Override public String getCorrelationId() { return "WARMUP"; }
                    @Override public String getOperationType() { return operation; }
                    @Override public Set<String> getTags() { return Set.of("default"); }
                };
                pipelineService.create(warmupMetadata);
                log.info("Pipeline [{}] aquecido com sucesso.", operation);
            } catch (Exception e) {
                log.error("Falha ao aquecer pipeline [{}]: {}", operation, e.getMessage());
            }
        }
    }
}
