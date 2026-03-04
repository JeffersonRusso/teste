package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;

@RequiredArgsConstructor
public class ScopeDecorator implements TaskDecorator {
    private final String nodeId;

    @Override
    public TaskResult apply(TaskChain next) {
        return ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId).get(() -> {
            ThreadContext.put("nodeId", nodeId);
            try {
                return next.proceed();
            } finally {
                ThreadContext.remove("nodeId");
            }
        });
    }
}
