package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.core.pipeline.selector.TaskSelectorStrategy;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.cache.GlobalDataCache;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mestre da montagem técnica do Pipeline.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineAssembler {

    private static final Set<String> DEFAULT_TARGETS = Set.of("resultado_final");

    private final TaskCatalogProvider taskProvider;
    private final TaskSelectorStrategy selectorStrategy;
    private final SystemHealthMonitor healthMonitor;
    private final PipelineTimeoutCalculator timeoutCalculator;
    private final GlobalDataCache globalCache;

    public Pipeline assemble(final ExecutionContext context, final FlowDefinition flowDef) {
        // 1. Seleciona as tasks que podem rodar
        final List<TaskDefinition> selected = selectTasks(context, flowDef);
        
        // 2. Coleta as chaves iniciais (Input + Globais + Tasks Globais que VÃO rodar)
        Set<String> initialKeys = new HashSet<>(context.asMap().keySet());
        initialKeys.addAll(globalCache.getAll().keySet());
        
        // Adiciona as chaves que as tasks globais produzem (mesmo que ainda não tenham rodado)
        taskProvider.findAllActive().stream()
                .filter(TaskDefinition::isGlobal)
                .forEach(t -> {
                    initialKeys.add(t.getNodeId().value());
                    if (t.getProduces() != null) t.getProduces().forEach(p -> initialKeys.add(p.name()));
                });
        
        if (selected.isEmpty()) {
            return new Pipeline(Collections.emptyList(), Duration.ZERO, 
                    flowDef != null ? flowDef.requiredOutputs() : DEFAULT_TARGETS, initialKeys);
        }
        
        final Set<String> requiredOutputs = (flowDef != null && flowDef.requiredOutputs() != null) 
                ? flowDef.requiredOutputs() 
                : DEFAULT_TARGETS;
        
        final Duration dynamicTimeout = timeoutCalculator.calculate(selected);
        
        log.info("Pipeline montado para [{}]: {} tarefas", context.getOperationType(), selected.size());

        return new Pipeline(selected, dynamicTimeout, requiredOutputs, initialKeys);
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
        return def.getCriticality() >= cutoffScore;
    }
}
