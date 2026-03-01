package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CircuitBreakerInterceptor implements TaskDecorator {

    private final CircuitBreaker circuitBreaker;

    @Override
    public TaskResult apply(TaskChain next) {
        return circuitBreaker.executeSupplier(next::proceed);
    }
}
