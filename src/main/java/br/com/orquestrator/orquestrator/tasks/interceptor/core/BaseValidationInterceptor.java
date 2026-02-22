package br.com.orquestrator.orquestrator.tasks.interceptor.core;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * BaseValidationInterceptor: Padroniza a validação de resultados.
 * Agora com assinatura simplificada e focada no dado.
 */
public abstract class BaseValidationInterceptor<C> extends TypedTaskInterceptor<C> {

    protected BaseValidationInterceptor(Class<C> configClass) {
        super(configClass);
    }

    @Override
    protected final TaskResult interceptTyped(ExecutionContext context, TaskChain next, C config, String nodeId) {
        // 1. Executa a task
        TaskResult result = next.proceed(context);

        // 2. Aplica a validação (Subclasses implementam apenas a lógica)
        validate(result, config, context);

        return result;
    }

    /**
     * Assinatura "Humana": O que validar (result), como validar (config) e onde reportar (context).
     */
    protected abstract void validate(TaskResult result, C config, ExecutionContext context);
}
