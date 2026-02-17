package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.script.SpelTask;
import br.com.orquestrator.orquestrator.tasks.script.SpelTaskConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpelTaskProvider implements TaskProvider {

    private final ExpressionService expressionService;

    @Override
    public String getType() {
        return "SPEL";
    }

    @Override
    public Task create(TaskDefinition def) {
        SpelTaskConfiguration config = parseConfiguration(def.getConfig(), def.getNodeId().value());
        return new SpelTask(def, expressionService, config);
    }

    private SpelTaskConfiguration parseConfiguration(JsonNode json, String nodeId) {
        if (json == null || json.isMissingNode()) {
            throw new TaskConfigurationException("Configuração SPEL ausente para a task: " + nodeId);
        }

        String expression = json.path("expression").asText();
        if (expression == null || expression.isBlank()) {
            throw new TaskConfigurationException("Expressão SPEL é obrigatória para a task: " + nodeId);
        }

        return new SpelTaskConfiguration(
            expression,
            json.path("required").asBoolean(true)
        );
    }
}
