package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
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
    protected void interceptTyped(TaskData data, TaskChain next, CircuitBreakerConfig config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();

        // Java 21: Uso de String Templates para identificação única da instância
        // O Registry gerencia o cache internamente baseado no nome e configuração
        CircuitBreaker cb = registry.circuitBreaker(
                STR."\{nodeId}_\{config.hashCode()}",
                () -> buildR4jConfig(config)
        );

        // Registro de estado inicial no rastro de metadados
        data.addMetadata("circuit_breaker.state", cb.getState().name());

        try {
            cb.executeRunnable(() -> next.proceed(data));
        } catch (CallNotPermittedException e) {
            handleShortCircuit(data, nodeId, cb);
        } catch (Exception e) {
            // Se for interrupção (Java 21 Virtual Threads), não deve contar como falha no CB
            if (e instanceof InterruptedException || Thread.currentThread().isInterrupted()) {
                throw e;
            }
            throw e;
        }
    }

    private void handleShortCircuit(TaskData data, String nodeId, CircuitBreaker cb) {
        data.addMetadata("circuit_breaker.short_circuited", true);

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
                // Garante que o Circuit Breaker ignore interrupções na contagem de erros
                .ignoreExceptions(InterruptedException.class)
                .build();
    }
}