package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogResponseDecoratorFactory implements DecoratorFactory<LogResponseConfig> {

    @Override
    public String getType() {
        return "LOGGING";
    }

    @Override
    public Class<LogResponseConfig> getConfigClass() {
        return LogResponseConfig.class;
    }

    @Override
    public TaskDecorator create(LogResponseConfig config, String nodeId) {
        return new LogResponseInterceptor(config, nodeId);
    }
}
