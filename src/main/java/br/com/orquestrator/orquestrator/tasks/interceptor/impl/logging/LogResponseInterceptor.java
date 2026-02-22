package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.core.TypedTaskInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LogResponseInterceptor: Otimizado para evitar alocações de String em alta carga.
 */
@Slf4j
@Component("LOG_RESPONSE_INTERCEPTOR")
public class LogResponseInterceptor extends TypedTaskInterceptor<LogResponseConfig> {

    public LogResponseInterceptor() {
        super(LogResponseConfig.class);
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, LogResponseConfig config, String nodeId) {
        TaskResult result = next.proceed(context);

        // Otimização: Checa nível de log antes de qualquer processamento
        if (config != null && !"OFF".equalsIgnoreCase(config.getLevel())) {
            executeLogging(nodeId, config, result);
        }
        return result;
    }

    private void executeLogging(String nodeId, LogResponseConfig config, TaskResult result) {
        // Otimização: Usa log parametrizado para evitar concatenação de String
        if ("DEBUG".equalsIgnoreCase(config.getLevel())) {
            if (log.isDebugEnabled()) {
                log.debug("Task '{}' finished with status: {}", nodeId, result.status());
                if (config.isShowBody() && result.body() != null) {
                    log.debug("   Response Body: {}", result.body());
                }
            }
        } else if ("WARN".equalsIgnoreCase(config.getLevel())) {
            log.warn("Task '{}' finished with status: {}", nodeId, result.status());
        } else if ("ERROR".equalsIgnoreCase(config.getLevel())) {
            log.error("Task '{}' finished with status: {}", nodeId, result.status());
        } else {
            if (log.isInfoEnabled()) {
                log.info("Task '{}' finished with status: {}", nodeId, result.status());
            }
        }
    }
}
