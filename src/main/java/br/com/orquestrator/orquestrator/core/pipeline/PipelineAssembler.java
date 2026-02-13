package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.core.pipeline.selector.TaskSelectorStrategy;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Mestre da montagem técnica do Pipeline.
 * Foca na seleção, filtragem e otimização das tarefas baseada no FlowDefinition fornecido.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineAssembler {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");

    private final TaskCatalogProvider taskProvider;
    private final TaskSelectorStrategy selectorStrategy;
    private final SystemHealthMonitor healthMonitor;
    private final TaskGraphFilter graphFilter;
    private final PipelineTimeoutCalculator timeoutCalculator;

    /**
     * Monta o Pipeline técnico a partir de uma definição de fluxo.
     */
    public Pipeline assemble(final ExecutionContext context, final FlowDefinition flowDef) {
        // 1. Seleção: Filtra o que pode rodar baseado no contexto e saúde do sistema
        final List<TaskDefinition> selected = selectTasks(context, flowDef);
        
        if (selected.isEmpty()) {
            log.warn("Nenhuma task selecionada para o contexto: {}", context.getOperationType());
            return new Pipeline(Collections.emptyList(), Duration.ZERO, 
                    flowDef != null ? flowDef.requiredOutputs() : DEFAULT_TARGETS);
        }
        
        // Java 21: Garantia de alvos não nulos
        final Set<String> requiredOutputs = (flowDef != null && flowDef.requiredOutputs() != null) 
                ? flowDef.requiredOutputs() 
                : DEFAULT_TARGETS;
        
        // 2. Otimização: Filtra apenas o necessário para os outputs requeridos (Graph Pruning)
        final List<TaskDefinition> optimizedTasks = graphFilter.filterByDependencies(selected, requiredOutputs);
        
        // 3. Orçamento: Calcula o tempo limite dinâmico baseado nas tarefas otimizadas
        final Duration dynamicTimeout = timeoutCalculator.calculate(optimizedTasks);
        
        log.info("Pipeline montado para [{}]: {} tarefas, Timeout: {}ms", 
                context.getOperationType(), optimizedTasks.size(), dynamicTimeout.toMillis());

        return new Pipeline(optimizedTasks, dynamicTimeout, requiredOutputs);
    }

    private List<TaskDefinition> selectTasks(final ExecutionContext context, final FlowDefinition flowDef) {
        final int currentCutoffScore = healthMonitor.getCutoffScore(context.getOperationType());

        return taskProvider.findAllActive().stream()
                .filter(def -> !def.isGlobal())
                .filter(def -> isAllowedInFlow(def, flowDef))
                .filter(def -> isCriticalEnough(def, currentCutoffScore))
                .filter(def -> selectorStrategy.shouldRun(def, context))
                .toList();
    }
    
    private boolean isAllowedInFlow(TaskDefinition def, FlowDefinition flowDef) {
        if (flowDef == null || flowDef.allowedTasks() == null) return true;

        return flowDef.allowedTasks().stream()
                .anyMatch(ref -> ref.id().equals(def.getNodeId().value()) && 
                                 ref.version().equals(def.getVersion()));
    }

    private boolean isCriticalEnough(final TaskDefinition def, final int cutoffScore) {
        if (def.getCriticality() < cutoffScore) {
            log.debug("Task {} (Score {}) removida por Load Shedding (Corte: {})", 
                    def.getNodeId(), def.getCriticality(), cutoffScore);
            return false;
        }
        return true;
    }
}
