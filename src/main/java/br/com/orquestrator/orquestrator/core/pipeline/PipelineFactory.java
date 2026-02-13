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
 * Atua como a fachada que utiliza o FlowRouter para decidir o caminho e o PipelineAssembler para montar o trilho.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineFactory {

    private final FlowRouter flowRouter;
    private final PipelineAssembler pipelineAssembler;

    /**
     * Cria um pipeline baseado no contexto de execução.
     * O FlowRouter decide qual FlowDefinition usar.
     */
    public Pipeline create(final ExecutionContext context) {
        FlowDefinition flowDef = flowRouter.route(context.getOperationType());
        return pipelineAssembler.assemble(context, flowDef);
    }

    /**
     * Cria um pipeline com alvos (outputs) específicos, sobrescrevendo a definição do fluxo.
     */
    public Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs) {
        FlowDefinition flowDef = flowRouter.route(context.getOperationType());
        
        // Java 21: Cria uma nova definição com os outputs customizados mantendo as tasks permitidas
        FlowDefinition customFlow = new FlowDefinition(
                flowDef.operationType(), 
                requiredOutputs, 
                flowDef.allowedTasks()
        );
        
        return pipelineAssembler.assemble(context, customFlow);
    }

    /**
     * Mantém compatibilidade com chamadas que passam versão explicitamente, 
     * embora o FlowRouter agora deva gerenciar isso internamente.
     */
    @Deprecated
    public Pipeline create(final ExecutionContext context, Integer version) {
        return create(context);
    }

    @Deprecated
    public Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs, Integer version) {
        return create(context, requiredOutputs);
    }
}
