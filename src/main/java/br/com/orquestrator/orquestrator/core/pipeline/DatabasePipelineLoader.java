package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * DatabasePipelineLoader: Carrega definições de pipeline do banco de dados.
 * Agora desacoplado do ExecutionContext e focado na identidade da requisição.
 */
@Component
@RequiredArgsConstructor
public class DatabasePipelineLoader implements PipelineLoader {

    private final PipelineRepository repository;

    @Override
    public Optional<PipelineDefinition> load(RequestIdentity identity) {
        return repository.findActive(identity.getOperationType());
    }

    @Override
    public boolean supports(RequestIdentity identity) {
        // Por padrão, se não for legado, tenta o banco
        return !identity.getTags().contains("legacy");
    }
}
