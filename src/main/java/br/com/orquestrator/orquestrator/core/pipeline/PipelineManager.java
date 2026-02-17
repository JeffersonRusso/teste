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

    private final PipelineFactory pipelineFactory;

    public Pipeline createAndValidate(ExecutionContext context, Set<String> requiredOutputs, Integer version) {
        // A validação e a poda agora acontecem dentro do construtor do Pipeline
        return pipelineFactory.create(context, requiredOutputs, version);
    }
}
