package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PipelineManager {

    private final PipelineFactory pipelineFactory;
    private final PipelineValidator validator;

    public Pipeline createAndValidate(ExecutionContext context, Set<String> requiredOutputs, Integer version) {
        Pipeline pipeline = pipelineFactory.create(context, requiredOutputs, version);
        validator.validate(pipeline, context);
        return pipeline;
    }
}
