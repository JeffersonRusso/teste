package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Gerenciador de Pipeline: Simplificado para apenas delegar a criação.
 */
@Service
@RequiredArgsConstructor
public class PipelineManager {

    private final PipelineService pipelineService;

    public Pipeline createAndValidate(ExecutionContext context, Set<String> requiredOutputs, Integer version) {
        return pipelineService.create(context, requiredOutputs);
    }
}
