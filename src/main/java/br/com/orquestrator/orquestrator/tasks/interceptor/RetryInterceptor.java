package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component("RETRY")
public class RetryInterceptor extends TypedTaskInterceptor<RetryConfig> {

    private final Map<String, Retry> retryCache = new ConcurrentHashMap<>();

    public RetryInterceptor() {
        super(RetryConfig.class);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, RetryConfig config, TaskDefinition taskDef) {
        if (config.maxAttempts() <= 1) {
            next.proceed(data);
            return;
        }

        String cacheKey = STR."\{taskDef.getNodeId().value()}_\{config.hashCode()}";
        Retry retry = retryCache.computeIfAbsent(cacheKey, k -> createRetry(taskDef.getNodeId().value(), config));

        retry.executeRunnable(() -> {
            try {
                next.proceed(data);
                data.addMetadata("retry.attempts", retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt() + 1);
            } catch (Exception e) {
                data.addMetadata("retry.last_error", e.getMessage());
                throw e;
            }
        });
    }

    private Retry createRetry(String name, RetryConfig config) {
        io.github.resilience4j.retry.RetryConfig r4jConfig = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .retryExceptions(Exception.class)
                .build();

        return Retry.of(name, r4jConfig);
    }
}
