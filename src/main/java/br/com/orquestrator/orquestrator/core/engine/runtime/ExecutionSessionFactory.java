package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.observability.PipelineEventPublisher;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExecutionSessionFactory {

    private final DataMarshaller marshaller;
    private final ReactiveExecutionEngine engine;
    private final PipelineEventPublisher eventPublisher;

    public ExecutionSession create(ExecutionContext context, Pipeline pipeline) {
        return new ExecutionSession(context, pipeline, marshaller, engine, eventPublisher);
    }
}
