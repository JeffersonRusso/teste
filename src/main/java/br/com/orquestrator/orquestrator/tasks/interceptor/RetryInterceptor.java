package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Interceptor responsável pela lógica de retentativas (Retry).
 */
@Slf4j
@Component("RETRY")
public class RetryInterceptor extends TypedTaskInterceptor<RetryConfig> {

    private final RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

    public RetryInterceptor() {
        super(RetryConfig.class);
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, RetryConfig config, TaskDefinition taskDef) {
        if (config.maxAttempts() <= 1) {
            return next.proceed(context);
        }

        String taskId = taskDef.getNodeId().value();
        String retryName = STR."\{taskId}_\{config.hashCode()}";
        
        Retry retry = retryRegistry.retry(retryName, () -> createR4jConfig(config));

        return retry.executeSupplier(() -> {
            try {
                Object result = next.proceed(context);
                if (retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt() > 0) {
                    context.track(taskId, "retry.status", "SUCCESS_WITH_RETRY");
                    context.track(taskId, "retry.attempts", retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
                }
                return result;
            } catch (Exception e) {
                context.track(taskId, "retry.last_error", e.getMessage());
                throw e; 
            }
        });
    }

    private io.github.resilience4j.retry.RetryConfig createR4jConfig(RetryConfig config) {
        return io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .retryExceptions(Exception.class)
                .build();
    }
}
