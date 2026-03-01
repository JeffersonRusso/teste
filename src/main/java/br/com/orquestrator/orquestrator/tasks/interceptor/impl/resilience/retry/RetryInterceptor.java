package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RetryInterceptor implements TaskDecorator {

    private final Retry retry;

    @Override
    public TaskResult apply(TaskChain next) {
        return retry.executeSupplier(next::proceed);
    }
}
