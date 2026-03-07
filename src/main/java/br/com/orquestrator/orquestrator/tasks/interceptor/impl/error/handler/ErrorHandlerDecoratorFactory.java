package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.handler;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlerDecoratorFactory implements DecoratorFactory<ErrorHandlerConfig> {

    @Override public String getType() { return "ERROR_HANDLER"; }
    @Override public Class<ErrorHandlerConfig> getConfigClass() { return ErrorHandlerConfig.class; }

    @Override
    public TaskInterceptor create(ErrorHandlerConfig config, String nodeId) {
        return new ErrorHandlerInterceptor(config, nodeId);
    }
}
