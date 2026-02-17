package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

/**
 * DmnTask: Puramente funcional.
 */
@RequiredArgsConstructor
public class DmnTask implements Task {

    private final TaskDefinition definition;
    private final DmnEngine dmnEngine;
    private final DmnTaskConfiguration config;

    @Override
    public Object execute(ExecutionContext context) {
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(config.decision(), context.asMap());
        return result.isEmpty() ? null : result.getFirstResult().getEntryMap();
    }
}
