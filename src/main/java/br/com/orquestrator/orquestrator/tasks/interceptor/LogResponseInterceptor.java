package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Interceptor responsável por logar a resposta da task.
 * Utiliza Java 21 String Templates para formatação eficiente e clara.
 */
@Slf4j
@Component("LOG_RESPONSE")
public class LogResponseInterceptor extends TypedTaskInterceptor<LogResponseConfig> {

    public LogResponseInterceptor() {
        super(LogResponseConfig.class);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, LogResponseConfig config, TaskDefinition taskDef) {
        // 1. Deixa a execução fluir
        next.proceed(data);

        // 2. Log Inteligente usando Java 21 String Templates
        if (shouldLog(config)) {
            executeLogging(data, config, taskDef);
        }
    }

    private void executeLogging(TaskData data, LogResponseConfig config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        Object status = data.getMetadata(TaskMetadataHelper.STATUS);
        Object body = config.isShowBody() ? data.getMetadata(TaskMetadataHelper.BODY) : "[REDACTED]";

        // Java 21: String Templates para mensagens de log ricas
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
