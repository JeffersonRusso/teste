package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GuardDecorator implements TaskDecorator {
    private final ExpressionEngine expressionEngine;
    private final String guardCondition;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        if (guardCondition == null || guardCondition.isBlank()) {
            return next.proceed(context);
        }

        if (!Boolean.TRUE.equals(expressionEngine.evaluate(guardCondition, ContextHolder.reader(), Boolean.class))) {
            log.debug("Task [{}] ignorada pela condição de guarda.", nodeId);
            return TaskResult.success(Map.of("skipped", true));
        }

        return next.proceed(context);
    }
}
