package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component("CIRCUIT_BREAKER")
public class CircuitBreakerInterceptor extends TypedTaskInterceptor<CircuitBreakerConfig> {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerInterceptor(CircuitBreakerRegistry registry) {
        super(CircuitBreakerConfig.class);
        this.registry = registry;
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, CircuitBreakerConfig config, TaskDefinition taskDef) {
        String cbName = taskDef.getNodeId().value();
        CircuitBreaker cb = registry.circuitBreaker(cbName, buildR4jConfig(config));

        data.addMetadata("circuit_breaker.state", cb.getState().name());

        try {
            cb.executeRunnable(() -> next.proceed(data));
        } catch (CallNotPermittedException e) {
            data.addMetadata("circuit_breaker.short_circuited", true);
            throw new PipelineException("Circuit Breaker OPEN para nó " + cbName, e)
                    .withNodeId(cbName)
                    .addMetadata("interceptor", "CIRCUIT_BREAKER")
                    .addMetadata("state", cb.getState().name());
        }
    }

    private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig buildR4jConfig(CircuitBreakerConfig config) {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationMs()))
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedCalls())
                .slidingWindowSize(config.getSlidingWindowSize())
                .build();
    }
}
