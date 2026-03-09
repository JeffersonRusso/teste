package br.com.orquestrator.orquestrator.tasks.script.spel;

import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * SpelTask: Executa expressões SpEL.
 */
@RequiredArgsConstructor
public class SpelTask implements Task {

    private final ExpressionEngine expressionEngine;
    private final SpelTaskConfiguration config;

    @Override
    public TaskResult execute(Map<String, JsonNode> inputs) {
        if (config.expression() == null || config.expression().isBlank()) {
            return TaskResult.success(MissingNode.getInstance());
        }

        JsonNode result = expressionEngine.compile(config.expression()).evaluate(inputs);
        return TaskResult.success(result);
    }
}
