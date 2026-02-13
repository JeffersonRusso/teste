package br.com.orquestrator.orquestrator.tasks.script;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpelTask extends AbstractTask {

    private final ExpressionService expressionService;
    private final TaskResultMapper resultMapper;
    private final SpelTaskConfiguration config;

    public SpelTask(TaskDefinition definition, 
                    ExpressionService expressionService,
                    TaskResultMapper resultMapper,
                    SpelTaskConfiguration config) {
        super(definition);
        this.expressionService = expressionService;
        this.resultMapper = resultMapper;
        this.config = config;
    }

    @Override
    public void validateConfig() {
        if (config.expression() == null || config.expression().isBlank()) {
            throw new IllegalArgumentException("SpelTask requires an 'expression' in config: " + definition.getNodeId().value());
        }
    }

    @Override
    public void execute(TaskData data) {
        try {
            // Usa a visão de Map para o contexto de avaliação (respeitando o contrato da ContractView)
            EvaluationContext evalContext = expressionService.create(data.asMap());
            Object result = evalContext.evaluate(config.expression(), Object.class);

            if (result == null && config.required()) {
                throw new PipelineException("Resultado da expressão SpEL é nulo, mas era obrigatório: " + config.expression());
            }

            resultMapper.mapResult(data, result, definition);
        } catch (Exception e) {
            throw new PipelineException("Erro ao avaliar expressão SpEL: " + config.expression(), e)
                    .withNodeId(definition.getNodeId().value());
        }
    }
}
