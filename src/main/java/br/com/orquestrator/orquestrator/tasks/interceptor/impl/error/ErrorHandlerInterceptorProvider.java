package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorHandlerInterceptorProvider implements InterceptorProvider<ErrorHandlerConfig> {

    private final ErrorHandlerInterceptor interceptor;

    @Override
    public String featureType() {
        return "ERROR_HANDLER";
    }

    @Override
    public Class<ErrorHandlerConfig> configClass() {
        return ErrorHandlerConfig.class;
    }

    @Override
    public TaskInterceptor create(ErrorHandlerConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
