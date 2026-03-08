package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Configurable;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * DmnTask: Executa tabelas de decisão DMN usando o Shadow Context.
 */
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
        Map<String, Object> rawInputs = new HashMap<>();
        context.inputs().forEach((k, v) -> rawInputs.put(k, v.raw()));

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, rawInputs);
        
        Object output = result.isEmpty() ? null : result.getFirstResult().getEntryMap();
        return TaskResult.success(DataValueFactory.of(output));
    }
}
