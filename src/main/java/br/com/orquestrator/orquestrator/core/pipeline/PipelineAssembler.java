package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * PipelineAssembler: Orquestra a montagem do pipeline delegando responsabilidades.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineAssembler {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");
    
    private final TaskCatalogProvider taskProvider;
    private final TaskSelector taskSelector;
    private final InitialKeyResolver keyResolver;
    private final PipelineTimeoutCalculator timeoutCalculator;
    private final PipelinePlanner pipelinePlanner;

    public Pipeline assemble(ExecutionContext context, FlowDefinition flowDef) {
        // 1. Seleção de Tasks
        List<TaskDefinition> selected = taskSelector.select(taskProvider.findAllActive(), context, flowDef);
        
        // 2. Resolução de Chaves Iniciais
        Set<String> initialKeys = keyResolver.resolve(context);
        
        // 3. Planejamento de Camadas (SOLID: SRP)
        List<List<TaskDefinition>> layers = pipelinePlanner.plan(selected, initialKeys);
        
        // 4. Definição de Outputs e Timeout
        Set<String> requiredOutputs = (flowDef != null && flowDef.requiredOutputs() != null) 
                ? flowDef.requiredOutputs() 
                : DEFAULT_TARGETS;
        
        Duration timeout = timeoutCalculator.calculate(selected);
        
        return new Pipeline(selected, timeout, requiredOutputs, layers);
    }
}
