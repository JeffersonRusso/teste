package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.util.Map;

@RequiredArgsConstructor
public class DmnTask implements Task {

    private final DmnEngine dmnEngine;
    private final DmnDecision decision;
    private final Map<String, Object> inputs; // Recebe os inputs já resolvidos

    @Override
    public TaskResult execute() {
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, inputs);
        Object output = result.isEmpty() ? null : result.getFirstResult().getEntryMap();
        return TaskResult.success(output);
    }
}
