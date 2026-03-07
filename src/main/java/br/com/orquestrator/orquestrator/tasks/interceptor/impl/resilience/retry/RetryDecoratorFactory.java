package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RetryDecoratorFactory implements DecoratorFactory<RetryConfig> {

    private final RetryRegistry retryRegistry;

    @Override public String getType() { return "RETRY"; }
    @Override public Class<RetryConfig> getConfigClass() { return RetryConfig.class; }

    @Override
    public TaskInterceptor create(RetryConfig config, String nodeId) {
        io.github.resilience4j.retry.RetryConfig retryConfig = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .build();

        Retry retry = retryRegistry.retry(nodeId, retryConfig);
        return new RetryInterceptor(retry);
    }
}
