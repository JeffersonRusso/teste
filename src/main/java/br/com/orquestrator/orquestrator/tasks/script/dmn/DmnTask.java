package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.util.Map;

@RequiredArgsConstructor
public class DmnTask implements Task, Configurable<DmnTaskConfiguration> {

    private final DmnEngine dmnEngine;
    private final DmnDecision decision;

    @Override
    public Class<DmnTaskConfiguration> getConfigClass() {
        return DmnTaskConfiguration.class;
    }

    @Override
    public TaskResult execute(TaskContext context) {
        // O DMN opera sobre o mapa de inputs locais da task
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, context.inputs());
        Object output = result.isEmpty() ? null : result.getFirstResult().getEntryMap();
        return TaskResult.success(output);
    }
}
