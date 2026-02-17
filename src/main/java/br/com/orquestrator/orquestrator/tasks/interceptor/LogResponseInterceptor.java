package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Interceptor responsável por logar a resposta da task.
 */
@Slf4j
@Component("LOG_RESPONSE")
public class LogResponseInterceptor extends TypedTaskInterceptor<LogResponseConfig> {

    public LogResponseInterceptor() {
        super(LogResponseConfig.class);
    }

    @Override
    protected Object interceptTyped(ExecutionContext context, TaskChain next, LogResponseConfig config, TaskDefinition taskDef) {
        Object result = next.proceed(context);

        if (shouldLog(config)) {
            executeLogging(context, config, taskDef, result);
        }
        return result;
    }

    private void executeLogging(ExecutionContext context, LogResponseConfig config, TaskDefinition taskDef, Object result) {
        String nodeId = taskDef.getNodeId().value();
        // Correção: Acesso via constante da ExecutionContext
        Object status = context.getMeta(nodeId, ExecutionContext.STATUS);
        Object body = config.isShowBody() ? result : "[REDACTED]";

        String message = STR."Task '\{nodeId}' finished with status: \{status != null ? status : "N/A"}";
        
        logAtLevel(config.getLevel(), message);

        if (config.isShowBody() && body != null) {
            logAtLevel(config.getLevel(), STR."   Response Body: \{body}");
        }
    }

    private void logAtLevel(String level, String message) {
        if (level == null) {
            log.info(message);
            return;
        }
        
        switch (level.toUpperCase()) {
            case "DEBUG" -> log.debug(message);
            case "WARN"  -> log.warn(message);
            case "ERROR" -> log.error(message);
            default      -> log.info(message);
        }
    }

    private boolean shouldLog(LogResponseConfig config) {
        return config != null && !"OFF".equalsIgnoreCase(config.getLevel());
    }
}
