package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

@RequiredArgsConstructor
public class ScopeDecorator implements TaskDecorator {

    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        try {
            return ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId).call(() -> {
                MDC.put("nodeId", nodeId);
                try {
                    return next.proceed(context);
                } finally {
                    MDC.remove("nodeId");
                }
            });
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
