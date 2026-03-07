package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GuardDecorator implements TaskInterceptor {
    private final ExpressionEngine expressionEngine;
    private final String guardCondition;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        if (guardCondition == null || guardCondition.isBlank()) {
            return chain.proceed(chain.context());
        }

        if (!Boolean.TRUE.equals(expressionEngine.compile(guardCondition).evaluate(ContextHolder.reader(), Boolean.class))) {
            log.debug("Task [{}] ignorada pela condição de guarda.", nodeId);
            return TaskResult.success(Map.of("skipped", true));
        }

        return chain.proceed(chain.context());
    }
}
