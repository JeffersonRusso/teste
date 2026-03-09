package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * DmnTask: Executa tabelas de decisão DMN.
 */
@RequiredArgsConstructor
public class DmnTask implements Task {

    private final DmnEngine dmnEngine;
    private final DmnDecision decision;

    @Override
    public TaskResult execute(Map<String, DataValue> inputs) {
        Map<String, Object> rawInputs = new HashMap<>();
        inputs.forEach((k, v) -> rawInputs.put(k, v.raw()));

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, rawInputs);
        
        Object output = result.isEmpty() ? null : result.getFirstResult().getEntryMap();
        return TaskResult.success(DataValueFactory.of(output));
    }
}
