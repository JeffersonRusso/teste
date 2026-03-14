package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ErrorHandlerInterceptor: Middleware responsável por aplicar a política de Fail-Fast.
 */
@Slf4j
@RequiredArgsConstructor
public final class ErrorHandlerInterceptor implements TaskInterceptor {

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        // CORREÇÃO DEMÉTER: Usa o atalho do contexto em vez de navegar na definição.
        boolean failFast = context.isFailFast();

        try {
            TaskResult result = chain.proceed(context);

            if (result instanceof TaskResult.Failure f && failFast) {
                log.error("Nó [{}] falhou (Fail-Fast Ativo): {}", context.getTaskName(), f.errorMessage());
                throw new RuntimeException(f.errorMessage());
            }

            return result;

        } catch (Exception e) {
            if (failFast) {
                log.error("Exceção crítica no nó [{}] (Fail-Fast Ativo): {}", context.getTaskName(), e.getMessage());
                throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
            }
            
            log.warn("Exceção no nó [{}] ignorada (Fail-Fast Inativo): {}", context.getTaskName(), e.getMessage());
            return new TaskResult.Failure(e.getMessage(), 500);
        }
    }
}
