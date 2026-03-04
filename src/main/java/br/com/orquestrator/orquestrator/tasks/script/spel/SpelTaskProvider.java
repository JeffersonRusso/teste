package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpelTaskProvider implements TaskProvider {

    private final ExpressionEngine expressionEngine;

    @Override
    public String getType() {
        return "SPEL";
    }

    @Override
    public Task create(TaskDefinition definition) {
        return new SpelTask(expressionEngine, definition);
    }
}
