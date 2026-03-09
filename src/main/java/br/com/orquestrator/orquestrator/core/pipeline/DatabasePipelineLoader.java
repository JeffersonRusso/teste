package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabasePipelineLoader implements PipelineLoader {

    private final PipelineRepository repository;

    @Override
    public PipelineDefinition loadDefinition(String operationType) {
        return repository.findActive(operationType)
                .orElseThrow(() -> new PipelineNotFoundException(operationType));
    }
}
