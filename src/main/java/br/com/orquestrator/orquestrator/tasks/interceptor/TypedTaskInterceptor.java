package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;

import java.util.concurrent.TimeUnit;

/**
 * Base para interceptadores tipados.
 */
public abstract class TypedTaskInterceptor<C> implements TaskInterceptor {

    private final Class<C> configClass;

    protected TypedTaskInterceptor(Class<C> configClass) {
        this.configClass = configClass;
    }

    @Override
    public Class<?> getConfigClass() {
        return configClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Object intercept(ExecutionContext context, TaskChain next, Object config, TaskDefinition taskDef) {
        String interceptorName = this.getClass().getSimpleName().replace("Interceptor", "");
        long start = System.nanoTime();
        
        try {
            return interceptTyped(context, next, (C) config, taskDef);
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            context.track(taskDef.getNodeId().value(), STR."interceptor.\{interceptorName}.duration_ms", duration);
        }
    }

    protected abstract Object interceptTyped(ExecutionContext context, TaskChain next, C config, TaskDefinition taskDef);
}
