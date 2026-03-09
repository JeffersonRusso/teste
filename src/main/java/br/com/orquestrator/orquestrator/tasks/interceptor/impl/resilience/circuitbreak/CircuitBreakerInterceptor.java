package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;

/**
 * CircuitBreakerInterceptor: Aplica o padrão Circuit Breaker.
 */
@RequiredArgsConstructor
public class CircuitBreakerInterceptor implements TaskInterceptor {

    private final CircuitBreaker circuitBreaker;

    @Override
    public TaskResult intercept(Chain chain) {
        return circuitBreaker.executeSupplier(() -> chain.proceed(chain.inputs()));
    }
}
