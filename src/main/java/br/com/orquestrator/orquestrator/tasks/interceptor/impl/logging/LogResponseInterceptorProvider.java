package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogResponseInterceptorProvider implements InterceptorProvider<LogResponseConfig> {

    private final LogResponseInterceptor interceptor;

    @Override
    public String featureType() {
        return "LOG_RESPONSE";
    }

    @Override
    public Class<LogResponseConfig> configClass() {
        return LogResponseConfig.class;
    }

    @Override
    public TaskInterceptor create(LogResponseConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
