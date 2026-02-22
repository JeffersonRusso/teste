package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CIRCUIT_BREAKER_INTERCEPTOR")
public class CircuitBreakerInterceptor extends TypedTaskInterceptor<CircuitBreakerConfig> {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerInterceptor(CircuitBreakerRegistry circuitBreakerRegistry) {
        super(CircuitBreakerConfig.class);
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, CircuitBreakerConfig config, String nodeId) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(nodeId);

        return cb.executeSupplier(() -> {
            try {
                return next.proceed(context);
            } catch (Throwable e) {
                throw e instanceof RuntimeException re ? re : new RuntimeException(e);
            }
        });
    }
}
