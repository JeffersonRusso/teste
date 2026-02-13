package br.com.orquestrator.orquestrator.tasks.script;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpelTask extends AbstractTask {

    private final ExpressionService expressionService;
    private final TaskResultMapper resultMapper;

    public SpelTask(TaskDefinition definition, 
                    ExpressionService expressionService,
                    TaskResultMapper resultMapper) {
        super(definition);
        this.expressionService = expressionService;
        this.resultMapper = resultMapper;
    }

    @Override
    public void validateConfig() {
        JsonNode config = definition.getConfig();
        if (!config.has("expression")) {
            throw new IllegalArgumentException("SpelTask requires an 'expression' in config: " + definition.getNodeId().value());
        }
    }

    @Override
    public void execute(TaskData data) {
        JsonNode config = definition.getConfig();
        String expression = config.path("expression").asText();

        try {
            // Usa a visão de Map para o contexto de avaliação
            EvaluationContext evalContext = expressionService.create(data.asMap());
            Object result = evalContext.evaluate(expression, Object.class);

            resultMapper.mapResult(data, result, definition);
        } catch (Exception e) {
            throw new PipelineException("Erro ao avaliar expressão SpEL: " + expression, e)
                    .withNodeId(definition.getNodeId().value());
        }
    }
}
