package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * PipelineService: Orquestrador do ciclo de vida do pipeline.
 * Agora simplificado para usar RequestIdentity (Dataflow).
 */
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineResolver resolver;
    private final PipelineRegistry registry;

    /**
     * Resolve e recupera o pipeline (grafo) baseado na identidade da requisição.
     */
    public Pipeline create(RequestIdentity identity) {
        // Resolve a definição (SQL) -> Busca/Cria no Registro (Cache)
        var definition = resolver.resolve(identity);
        
        // O PipelineRegistry agora recebe as tags da identidade
        return registry.get(definition, identity.getTags());
    }
}
