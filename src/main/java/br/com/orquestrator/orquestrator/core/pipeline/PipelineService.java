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
 * PipelineService: Fachada única que gerencia o ciclo de vida do pipeline.
 * Focada em roteamento, cache e montagem.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final FlowRouter flowRouter;
    private final PipelineAssembler pipelineAssembler;
    private final PipelineCache pipelineCache;

    public Pipeline create(ExecutionContext context, Set<String> requiredOutputs) {
        // 1. Roteamento
        FlowDefinition flowDef = flowRouter.route(context.getOperationType());
        
        // 2. Tenta buscar do Cache (Se não houver customização de outputs)
        boolean isCustom = requiredOutputs != null && !requiredOutputs.isEmpty();
        String cacheKey = pipelineCache.generateKey(context.getOperationType(), flowDef.version());
        
        if (!isCustom) {
            Pipeline cached = pipelineCache.get(cacheKey);
            if (cached != null) return cached;
        }

        // 3. Montagem
        FlowDefinition finalFlow = isCustom
                ? new FlowDefinition(flowDef.operationType(), flowDef.version(), requiredOutputs, flowDef.allowedTasks())
                : flowDef;

        Pipeline pipeline = pipelineAssembler.assemble(context, finalFlow);

        // 4. Salva no Cache se não for customizado
        if (!isCustom) {
            pipelineCache.put(cacheKey, pipeline);
        }

        return pipeline;
    }
}
