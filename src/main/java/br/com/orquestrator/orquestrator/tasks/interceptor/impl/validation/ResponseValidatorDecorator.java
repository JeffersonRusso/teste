package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ResponseValidatorDecorator: Valida o resultado da tarefa contra regras SpEL.
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseValidatorDecorator implements TaskInterceptor {

    private final ResponseValidatorConfig config;
    private final String nodeId;
    private final ExpressionEngine expressionEngine;

    @Override
    public TaskResult intercept(Chain chain) {
        TaskResult result = chain.proceed(chain.inputs());

        if (result != null && result.isSuccess()) {
            validate(result);
        }

        return result;
    }

    private void validate(TaskResult result) {
        if (config == null || config.rules() == null) return;

        Map<String, Object> evalRoot = Map.of("result", result);

        for (var rule : config.rules()) {
            Boolean isValid = expressionEngine.compile(rule.condition()).evaluate(evalRoot, Boolean.class);
            
            if (Boolean.FALSE.equals(isValid)) {
                log.error("Falha na validação de resposta do nó [{}]: {}", nodeId, rule.message());
                throw new PipelineException(rule.message()).withNodeId(nodeId);
            }
        }
    }
}
