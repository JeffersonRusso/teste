package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import org.springframework.stereotype.Component;

@Component
public class LogResponseDecoratorFactory implements DecoratorFactory<LogResponseConfig> {

    @Override public String getType() { return "LOGGING"; }
    @Override public Class<LogResponseConfig> getConfigClass() { return LogResponseConfig.class; }

    @Override
    public TaskInterceptor create(LogResponseConfig config, String nodeId) {
        return new LogResponseInterceptor(config, nodeId);
    }
}
