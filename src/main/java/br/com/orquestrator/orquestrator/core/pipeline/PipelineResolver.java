package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PipelineResolver: Único responsável por decidir qual definição de pipeline usar.
 * Aplica o Strategy Pattern para desacoplar a origem dos dados.
 */
@Component
@RequiredArgsConstructor
public class PipelineResolver {

    private final List<PipelineLoader> loaders;

    public PipelineDefinition resolve(ContextMetadata metadata) {
        return loaders.stream()
                .filter(l -> l.supports(metadata))
                .findFirst()
                .flatMap(l -> l.load(metadata))
                .orElseThrow(() -> new PipelineException("Nenhuma definição de pipeline encontrada para: " + metadata.getOperationType()));
    }
}
