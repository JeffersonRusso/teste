package br.com.orquestrator.orquestrator.tasks.interceptor.core;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;

/**
 * Base para interceptadores tipados.
 */
public abstract class TypedTaskInterceptor<C> {

    private final Class<C> configClass;

    protected TypedTaskInterceptor(Class<C> configClass) {
        this.configClass = configClass;
    }

    public Class<C> getConfigClass() {
        return configClass;
    }

    /**
     * Método interno com nodeId simplificado.
     */
    protected abstract TaskResult interceptTyped(ExecutionContext context, TaskChain next, C config, String nodeId);

    public TaskInterceptor adapt(C config, String nodeId) {
        return next -> context -> interceptTyped(context, next, config, nodeId);
    }
}
