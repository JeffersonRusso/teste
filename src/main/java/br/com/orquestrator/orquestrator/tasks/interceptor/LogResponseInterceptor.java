package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
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
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, LogResponseConfig config, TaskDefinition taskDef) {
        TaskResult result = next.proceed(context);

        if (shouldLog(config)) {
            executeLogging(context, config, taskDef, result);
        }
        return result;
    }

    private void executeLogging(ExecutionContext context, LogResponseConfig config, TaskDefinition taskDef, TaskResult result) {
        String nodeId = taskDef.getNodeId().value();
        int status = result.status();
        Object body = config.isShowBody() ? result.body() : "[REDACTED]";

        String message = STR."Task '\{nodeId}' finished with status: \{status}";
        
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
