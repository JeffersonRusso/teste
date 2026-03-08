package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PipelineResolver: Único responsável por decidir qual definição de pipeline usar.
 * Agora usa RequestIdentity para desacoplar do ExecutionContext.
 */
@Component
@RequiredArgsConstructor
public class PipelineResolver {

    private final List<PipelineLoader> loaders;

    public PipelineDefinition resolve(RequestIdentity identity) {
        return loaders.stream()
                .filter(l -> l.supports(identity))
                .findFirst()
                .flatMap(l -> l.load(identity))
                .orElseThrow(() -> new PipelineException("Nenhuma definição de pipeline encontrada para: " + identity.getOperationType()));
    }
}
