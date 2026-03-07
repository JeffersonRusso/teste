package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

/**
 * TaskInterceptor: Unidade de lógica linear.
 */
public interface TaskInterceptor {
    TaskResult intercept(Chain chain);

    interface Chain {
        TaskContext context();
        TaskResult proceed(TaskContext context);
    }
}
