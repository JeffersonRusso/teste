package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("LOG_RESPONSE")
public class LogResponseInterceptor extends TypedTaskInterceptor<LogResponseConfig> {

    public LogResponseInterceptor() {
        super(LogResponseConfig.class);
    }

    @Override
    protected void interceptTyped(TaskData data, TaskChain next, LogResponseConfig config, TaskDefinition taskDef) {
        next.proceed(data);
        logResult(data, config, taskDef);
    }

    private void logResult(TaskData data, LogResponseConfig config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        String message = String.format("Task '%s' finished.", nodeId);
        
        switch (config.getLevel().toUpperCase()) {
            case "DEBUG" -> log.debug(message);
            case "WARN" -> log.warn(message);
            case "ERROR" -> log.error(message);
            default -> log.info(message);
        }

        if (config.isShowBody()) {
            List<DataSpec> produces = taskDef.getProduces();
            if (produces != null) {
                for (int i = 0; i < produces.size(); i++) {
                    String varName = produces.get(i).name();
                    log.info("   -> Output [{}]: {}", varName, data.get(varName));
                }
            }
        }
    }
}
