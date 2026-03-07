package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * PipelineService: Orquestrador do ciclo de vida do pipeline.
 * SOLID: Desacoplado da lógica de resolução e de registro.
 */
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineResolver resolver;
    private final PipelineRegistry registry;

    public Pipeline create(ContextMetadata metadata) {
        // Fluxo linear: Resolve a definição -> Busca/Cria no Registro
        var definition = resolver.resolve(metadata);
        return registry.get(definition, metadata.getTags());
    }
}
