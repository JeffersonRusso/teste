package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

/**
 * DmnTask: Opera diretamente sobre o Map do contexto.
 */
@RequiredArgsConstructor
public class DmnTask implements Task {

    private final TaskDefinition definition;
    private final DmnEngine dmnEngine;
    private final DmnTaskConfiguration config;
    private final DmnDecision decision;

    @Override
    public TaskResult execute(ExecutionContext context) {
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, context.getRoot());
        Object output = result.isEmpty() ? null : result.getFirstResult().getEntryMap();
        return TaskResult.success(output);
    }
}
