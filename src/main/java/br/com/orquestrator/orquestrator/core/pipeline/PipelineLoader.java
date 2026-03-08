package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import java.util.Optional;

/**
 * PipelineLoader: Interface para carregamento de definições de pipeline.
 * Agora desacoplada do ExecutionContext e focada na identidade da requisição.
 */
public interface PipelineLoader {
    Optional<PipelineDefinition> load(RequestIdentity identity);
    boolean supports(RequestIdentity identity);
}
