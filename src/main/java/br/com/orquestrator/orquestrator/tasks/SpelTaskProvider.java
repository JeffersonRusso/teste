package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import br.com.orquestrator.orquestrator.tasks.script.SpelTask;
import br.com.orquestrator.orquestrator.tasks.script.SpelTaskConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
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
        // 1. O Provider interpreta a "sujeira" do JSON
        SpelTaskConfiguration config = parseConfiguration(def.getConfig(), def.getNodeId().value());
        
        // 2. Instancia a Task passando a configuração já tipada
        return new SpelTask(def, expressionService, resultMapper, config);
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
