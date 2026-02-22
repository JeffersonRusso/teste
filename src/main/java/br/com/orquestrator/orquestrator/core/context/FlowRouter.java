package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.core.pipeline.OrchestratorMetadataStore;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlowRouter {

    private final OrchestratorMetadataStore metadataStore;

    public FlowDefinition route(String operationType) {
        // LÊ DA MEMÓRIA (Latência Zero)
        return metadataStore.getFlow(operationType)
                .orElseThrow(() -> new PipelineException("Fluxo não encontrado em memória para: " + operationType));
    }
}
