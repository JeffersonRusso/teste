package br.com.orquestrator.orquestrator.tasks.interceptor.resilience;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
public class RetryStrategy implements ResilienceStrategy<RetryConfig> {

    private final RetryRegistry registry = RetryRegistry.ofDefaults();

    @Override
    public TaskResult execute(Supplier<TaskResult> execution, String resourceId, RetryConfig config) {
        String name = STR."\{resourceId}_\{config.hashCode()}";
        Retry retry = registry.retry(name, () -> io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .retryExceptions(Exception.class)
                .build());

        return retry.executeSupplier(execution);
    }

    @Override
    public String getType() { return "RETRY"; }
}
