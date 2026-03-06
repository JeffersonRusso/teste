package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.ContextMetadata;
import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final List<PipelineLoader> loaders;
    private final PipelineRegistry registry;

    public Pipeline create(ContextMetadata metadata) {
        // Encontra o carregador baseado nos metadados (incluindo tags)
        PipelineDefinition def = loaders.stream()
                .filter(l -> l.supports(metadata))
                .findFirst()
                .flatMap(l -> l.load(metadata))
                .orElseThrow(() -> new PipelineException("Nenhuma definição de pipeline encontrada para: " + metadata.getOperationType()));

        return registry.get(def, metadata.getTags());
    }
}
