package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Base para interceptadores tipados com suporte nativo a observabilidade via TaskData.
 */
@Slf4j
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
    public final void intercept(TaskData data, TaskChain next, Object config, TaskDefinition taskDef) {
        String interceptorName = this.getClass().getSimpleName().replace("Interceptor", "");
        long start = System.nanoTime();
        
        try {
            interceptTyped(data, next, (C) config, taskDef);
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            data.addMetadata(STR."interceptor.\{interceptorName}.duration_ms", duration);
        }
    }

    protected abstract void interceptTyped(TaskData data, TaskChain next, C config, TaskDefinition taskDef);
}
