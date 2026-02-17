package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.pipeline.selector.TaskSelectorStrategy;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.health.SystemHealthMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TaskSelector: Especialista em filtrar quais tasks devem compor o pipeline.
 */
@Component
@RequiredArgsConstructor
public class TaskSelector {

    private final SystemHealthMonitor healthMonitor;
    private final TaskSelectorStrategy selectorStrategy;

    public List<TaskDefinition> select(List<TaskDefinition> allTasks, ExecutionContext context, FlowDefinition flowDef) {
        final int cutoffScore = healthMonitor.getCutoffScore(context.getOperationType());

        return allTasks.stream()
                .filter(def -> !def.isGlobal())
                .filter(def -> isAllowedInFlow(def, flowDef))
                .filter(def -> def.getCriticality() >= cutoffScore)
                .filter(def -> selectorStrategy.shouldRun(def, context))
                .toList();
    }

    private boolean isAllowedInFlow(TaskDefinition def, FlowDefinition flowDef) {
        if (flowDef == null || flowDef.allowedTasks() == null) return true;
        return flowDef.allowedTasks().stream()
                .anyMatch(ref -> ref.id().equals(def.getNodeId().value()) && 
                                 ref.version().equals(def.getVersion()));
    }
}
