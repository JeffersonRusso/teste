package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScopeDecorator implements TaskInterceptor {
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        try {
            return ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId)
                    .call(() -> chain.proceed(chain.context()));
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
