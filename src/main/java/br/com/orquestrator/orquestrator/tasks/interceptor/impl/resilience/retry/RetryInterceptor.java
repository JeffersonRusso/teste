package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RetryInterceptor implements TaskInterceptor {

    private final Retry retry;

    @Override
    public TaskResult intercept(Chain chain) {
        return retry.executeSupplier(() -> chain.proceed(chain.context()));
    }
}
