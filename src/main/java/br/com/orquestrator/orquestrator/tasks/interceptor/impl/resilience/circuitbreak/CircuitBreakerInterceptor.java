package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.circuitbreak;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * CircuitBreakerInterceptor: Middleware de Resiliência para Proteção de Falhas em Cadeia.
 */
@Slf4j
@RequiredArgsConstructor
public final class CircuitBreakerInterceptor implements TaskInterceptor {

    private final CompiledConfiguration<CircuitBreakerConfig> config;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();
        CircuitBreakerConfig resolvedConfig = config.resolve(context.getInputs());
        
        float failureRate = resolvedConfig.failureRateThreshold() != null ? resolvedConfig.failureRateThreshold().floatValue() : 50.0f;
        long waitDuration = resolvedConfig.waitDurationInOpenStateMs() != null ? resolvedConfig.waitDurationInOpenStateMs() : 60000L;
        int windowSize = resolvedConfig.slidingWindowSize() != null ? resolvedConfig.slidingWindowSize() : 100;

        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig cbConfig = 
            io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRate)
                .waitDurationInOpenState(Duration.ofMillis(waitDuration))
                .slidingWindowSize(windowSize)
                .build();

        CircuitBreaker circuitBreaker = CircuitBreakerRegistry.of(cbConfig).circuitBreaker(nodeId);

        try {
            return circuitBreaker.executeSupplier(() -> chain.proceed(context));
        } catch (Exception e) {
            log.warn("Circuito aberto para o nó [{}]: {}", nodeId, e.getMessage());
            return new TaskResult.Failure("Circuito aberto: " + e.getMessage(), 500);
        }
    }
}
