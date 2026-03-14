package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * RetryInterceptor: Middleware de Resiliência para Tentativas Automáticas.
 */
@Slf4j
@RequiredArgsConstructor
public final class RetryInterceptor implements TaskInterceptor {

    private final CompiledConfiguration<RetryConfig> config;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();
        RetryConfig resolvedConfig = config.resolve(context.getInputs());
        
        // Adiciona valores padrão para evitar NullPointerException em tipos primitivos
        int maxAttempts = resolvedConfig.maxAttempts() > 0 ? resolvedConfig.maxAttempts() : 3;
        long waitDuration = resolvedConfig.waitDurationMs() > 0 ? resolvedConfig.waitDurationMs() : 500;

        io.github.resilience4j.retry.RetryConfig retryConfig = 
            io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDuration))
                .build();

        Retry retry = RetryRegistry.of(retryConfig).retry(nodeId);

        try {
            return retry.executeSupplier(() -> chain.proceed(context));
        } catch (Exception e) {
            log.error("Tentativas de retry esgotadas para o nó [{}]: {}", nodeId, e.getMessage());
            return new TaskResult.Failure(e.getMessage(), 500);
        }
    }
}
