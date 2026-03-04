package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * PipelineService: Orquestra a obtenção de pipelines.
 * Delega a compilação e o cache para o PipelineRegistry.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineRepository repository;
    private final PipelineRegistry registry;

    public Pipeline create(ContextMetadata metadata) {
        String operationType = metadata.getOperationType();

        // 1. Busca a definição ativa no banco (ou cache de definição)
        PipelineDefinition def = repository.findActive(operationType)
                .orElseThrow(() -> new PipelineException("Nenhum pipeline ativo encontrado para: " + operationType));

        // 2. Solicita o executável ao registro (que gerencia o cache de compilação)
        return registry.get(def, metadata.getTags());
    }
}
