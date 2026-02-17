package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component("CIRCUIT_BREAKER")
public class CircuitBreakerInterceptor extends TypedTaskInterceptor<CircuitBreakerConfig> {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerInterceptor(CircuitBreakerRegistry registry) {
        super(CircuitBreakerConfig.class);
        this.registry = registry;
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, CircuitBreakerConfig config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();

        CircuitBreaker cb = registry.circuitBreaker(
                STR."\{nodeId}_\{config.hashCode()}",
                () -> buildR4jConfig(config)
        );

        context.track(nodeId, "circuit_breaker.state", cb.getState().name());

        try {
            return cb.executeSupplier(() -> next.proceed(context));
        } catch (CallNotPermittedException e) {
            return handleShortCircuit(context, nodeId, cb);
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw e;
            }
            throw e;
        }
    }

    private Object handleShortCircuit(ExecutionContext context, String nodeId, CircuitBreaker cb) {
        context.track(nodeId, "circuit_breaker.short_circuited", true);
        log.warn(STR."   [CircuitBreaker] Short-circuit ativado para o nó: \{nodeId}");

        throw new PipelineException(STR."Circuit Breaker \{cb.getState()} para nó \{nodeId}")
                .withNodeId(nodeId)
                .addMetadata("interceptor", "CIRCUIT_BREAKER")
                .addMetadata("state", cb.getState().name());
    }

    private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig buildR4jConfig(CircuitBreakerConfig config) {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationMs()))
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedCalls())
                .slidingWindowSize(config.getSlidingWindowSize())
                .ignoreExceptions(InterruptedException.class)
                .build();
    }
}
