package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
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
    private final SpelContextFactory contextFactory;

    @Override
    public TaskResult apply(TaskChain next) {
        TaskResult result = next.proceed();
        
        ExecutionContext context = ContextHolder.CONTEXT.get();
        validate(result, context);

        return result;
    }

    private void validate(TaskResult result, ExecutionContext context) {
        if (config == null || config.rules() == null) return;
        
        // Cria um contexto de avaliação temporário injetando o resultado da task (#result)
        var evalContext = contextFactory.create(context, Map.of("result", result));

        for (var rule : config.rules()) {
            if (Boolean.TRUE.equals(evalContext.evaluate(rule.condition(), Boolean.class))) {
                throw new PipelineException(rule.message()).withNodeId(nodeId);
            }
        }
    }
}
