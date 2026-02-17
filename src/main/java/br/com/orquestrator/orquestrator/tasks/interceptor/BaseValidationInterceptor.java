package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * BaseValidationInterceptor: Padroniza interceptores que validam o resultado de uma task.
 */
public abstract class BaseValidationInterceptor<C> extends TypedTaskInterceptor<C> {

    protected BaseValidationInterceptor(Class<C> configClass) {
        super(configClass);
    }

    @Override
    protected final TaskResult interceptTyped(ExecutionContext context, TaskChain next, C config, TaskDefinition taskDef) {
        // 1. Executa a task (ou o próximo interceptor)
        TaskResult result = next.proceed(context);

        // 2. Aplica a validação sobre o resultado
        validate(result, config, context, taskDef);

        return result;
    }

    protected abstract void validate(TaskResult result, C config, ExecutionContext context, TaskDefinition taskDef);
}
