package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import br.com.orquestrator.orquestrator.tasks.script.SpelTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpelTaskProvider implements TaskProvider {

    private final ExpressionService expressionService;
    private final TaskResultMapper resultMapper;

    @Override
    public String getType() {
        return "SPEL";
    }

    @Override
    public Task create(TaskDefinition def) {
        return new SpelTask(def, expressionService, resultMapper);
    }
}
