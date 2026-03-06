package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabasePipelineLoader implements PipelineLoader {

    private final PipelineRepository repository;

    @Override
    public Optional<PipelineDefinition> load(ContextMetadata metadata) {
        return repository.findActive(metadata.getOperationType());
    }

    @Override
    public boolean supports(ContextMetadata metadata) {
        // Por padrão, se não for legado, tenta o banco
        return !metadata.getTags().contains("legacy");
    }
}
