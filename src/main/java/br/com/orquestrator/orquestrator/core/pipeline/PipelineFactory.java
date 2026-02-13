package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigProvider;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.core.pipeline.selector.TaskSelectorStrategy;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskReference;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineFactory {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");

    private final TaskCatalogProvider taskProvider;
    private final FlowConfigProvider flowConfigProvider;
    private final TaskSelectorStrategy selectorStrategy;
    private final SystemHealthMonitor healthMonitor;
    private final TaskGraphFilter graphFilter;
    private final PipelineTimeoutCalculator timeoutCalculator;

    public Pipeline create(final ExecutionContext context, Integer version) {
        var flowDef = flowConfigProvider.getFlow(context.getOperationType(), version);
        Set<String> targets = flowDef.map(FlowDefinition::requiredOutputs).orElse(DEFAULT_TARGETS);
        return create(context, targets, flowDef.orElse(null));
    }

    public Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs, Integer version) {
        var flowDef = flowConfigProvider.getFlow(context.getOperationType(), version);
        return create(context, requiredOutputs, flowDef.orElse(null));
    }

    private Pipeline create(final ExecutionContext context, final Set<String> requiredOutputs, final FlowDefinition flowDef) {
        final List<TaskDefinition> selected = selectTasks(context, flowDef);
        
        if (selected.isEmpty()) {
            log.warn("Nenhuma task selecionada para o contexto atual.");
            return new Pipeline(Collections.emptyList(), Duration.ZERO, requiredOutputs);
        }
        
        final Set<String> targets = CollectionUtils.isEmpty(requiredOutputs) ? DEFAULT_TARGETS : requiredOutputs;
        
        final List<TaskDefinition> optimizedTasks = graphFilter.filterByDependencies(selected, targets);
        final Duration dynamicTimeout = timeoutCalculator.calculate(optimizedTasks);
        
        return new Pipeline(optimizedTasks, dynamicTimeout, targets);
    }

    private List<TaskDefinition> selectTasks(final ExecutionContext context, final FlowDefinition flowDef) {
        final int currentCutoffScore = healthMonitor.getCutoffScore(context.getOperationType());

        return taskProvider.findAllActive().stream()
                .filter(def -> !def.isGlobal())
                .filter(def -> filterByFlow(def, flowDef))
                .filter(def -> isCriticalEnough(def, currentCutoffScore))
                .filter(def -> selectorStrategy.shouldRun(def, context))
                .toList();
    }
    
    private boolean filterByFlow(TaskDefinition def, FlowDefinition flowDef) {
        if (flowDef == null) return true;

        if (flowDef.allowedTasks() != null && !flowDef.allowedTasks().isEmpty()) {
            return flowDef.allowedTasks().stream()
                    .anyMatch(ref -> matches(ref, def));
        }
        
        return false;
    }
    
    private boolean matches(TaskReference ref, TaskDefinition def) {
        return ref.id().equals(def.getNodeId().value()) && 
               ref.version().equals(def.getVersion());
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
