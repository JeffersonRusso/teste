package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.FlowRouter;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Orquestrador da criação de Pipelines.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineFactory {

    private final FlowRouter flowRouter;
    private final PipelineAssembler pipelineAssembler;

    public Pipeline create(final ExecutionContext context) {
        FlowDefinition flowDef = flowRouter.route(context.getOperationType());
        return pipelineAssembler.assemble(context, flowDef);
    }

    public Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs) {
        FlowDefinition flowDef = flowRouter.route(context.getOperationType());
        
        // Atualizado para incluir a versão no construtor do FlowDefinition
        FlowDefinition customFlow = new FlowDefinition(
                flowDef.operationType(), 
                flowDef.version(),
                requiredOutputs, 
                flowDef.allowedTasks()
        );
        
        return pipelineAssembler.assemble(context, customFlow);
    }

    @Deprecated
    public Pipeline create(final ExecutionContext context, Integer version) {
        return create(context);
    }

    @Deprecated
    public Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs, Integer version) {
        return create(context, requiredOutputs);
    }
}
