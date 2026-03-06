package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import java.util.Optional;

public interface PipelineLoader {
    Optional<PipelineDefinition> load(ContextMetadata metadata);
    boolean supports(ContextMetadata metadata);
}
