package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ResponseValidatorDecorator implements TaskDecorator {

    private final ResponseValidatorConfig config;
    private final String nodeId;
    private final ExpressionEngine expressionEngine;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        // 1. Executa a task core
        TaskResult result = next.proceed(context);

        // 2. Valida o resultado
        if (result != null && result.isSuccess()) {
            validate(result);
        }

        return result;
    }

    private void validate(TaskResult result) {
        if (config == null || config.rules() == null) return;

        Map<String, Object> evalRoot = Map.of("result", result);

        for (var rule : config.rules()) {
            Boolean isValid = expressionEngine.evaluate(rule.condition(), evalRoot, Boolean.class);
            
            if (Boolean.FALSE.equals(isValid)) {
                log.error("Falha na validação de resposta do nó [{}]: {}", nodeId, rule.message());
                throw new PipelineException(rule.message()).withNodeId(nodeId);
            }
        }
    }
}
