package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Interceptor responsável pela lógica de retentativas (Retry).
 * Utiliza o Resilience4j para gerenciar as tentativas e o TaskData para rastro.
 * Java 21: Uso de String Templates e delegação para o RetryRegistry.
 */
@Slf4j
@Component("RETRY")
public class RetryInterceptor extends TypedTaskInterceptor<RetryConfig> {

    // Recomendação: Usar o Registry do próprio Resilience4j para gerenciar o ciclo de vida das instâncias
    private final RetryRegistry retryRegistry = RetryRegistry.ofDefaults();

    public RetryInterceptor() {
        super(RetryConfig.class);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, RetryConfig config, TaskDefinition taskDef) {
        // Fail Fast: Se não há múltiplas tentativas configuradas, segue o fluxo normal
        if (config.maxAttempts() <= 1) {
            next.proceed(data);
            return;
        }

        String taskId = taskDef.getNodeId().value();
        // Java 21: String Templates para gerar uma chave única baseada na configuração
        String retryName = STR."\{taskId}_\{config.hashCode()}";
        
        Retry retry = retryRegistry.retry(retryName, () -> createR4jConfig(config));

        retry.executeRunnable(() -> {
            try {
                next.proceed(data);
                // Se chegou aqui após retentativas, marcamos o sucesso no rastro
                if (retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt() > 0) {
                    data.addMetadata("retry.status", "SUCCESS_WITH_RETRY");
                    data.addMetadata("retry.attempts", retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
                }
            } catch (Exception e) {
                // Registramos o erro da última tentativa para depuração
                data.addMetadata("retry.last_error", e.getMessage());
                throw e; 
            }
        });
    }

    private io.github.resilience4j.retry.RetryConfig createR4jConfig(RetryConfig config) {
        return io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(config.maxAttempts())
                .waitDuration(Duration.ofMillis(config.waitDurationMs()))
                .retryExceptions(Exception.class) // Captura todas as exceções para retry por padrão
                .build();
    }
}
