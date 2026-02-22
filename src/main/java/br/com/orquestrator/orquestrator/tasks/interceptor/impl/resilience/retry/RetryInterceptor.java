package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RetryInterceptor: Otimizado para evitar alocações no caminho crítico.
 */
@Component("RETRY_INTERCEPTOR")
public class RetryInterceptor extends TypedTaskInterceptor<RetryConfig> {

    private final RetryRegistry retryRegistry;
    // Cache de instâncias de Retry para evitar recriação de configurações
    private final Map<String, Retry> retryCache = new ConcurrentHashMap<>();

    public RetryInterceptor(RetryRegistry retryRegistry) {
        super(RetryConfig.class);
        this.retryRegistry = retryRegistry;
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, RetryConfig config, String nodeId) {
        // Otimização: Lookup no cache local antes de qualquer alocação
        Retry retry = retryCache.computeIfAbsent(nodeId, _ -> createRetry(nodeId, config));

        // Execução direta para evitar criação de Supplier lambda se possível
        // (Embora o Resilience4j exija um Supplier, aqui ele é criado uma vez por task)
        return retry.executeSupplier(() -> {
            try {
                return next.proceed(context);
            } catch (Exception e) {
                throw e instanceof RuntimeException re ? re : new RuntimeException(e);
            }
        });
    }

    private Retry createRetry(String nodeId, RetryConfig config) {
        io.github.resilience4j.retry.RetryConfig resilienceConfig = io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .build();
        return retryRegistry.retry(nodeId, resilienceConfig);
    }
}
