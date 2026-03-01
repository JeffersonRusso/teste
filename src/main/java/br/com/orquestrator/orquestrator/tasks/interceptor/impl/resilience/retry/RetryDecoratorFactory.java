package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RetryDecoratorFactory implements DecoratorFactory<RetryConfig> {

    @Override
    public String getType() {
        return "RETRY";
    }

    @Override
    public Class<RetryConfig> getConfigClass() {
        return RetryConfig.class;
    }

    @Override
    public TaskDecorator create(RetryConfig config, String nodeId) {
        io.github.resilience4j.retry.RetryConfig r4jConfig = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .build();

        return new RetryInterceptor(Retry.of(nodeId, r4jConfig));
    }
}
