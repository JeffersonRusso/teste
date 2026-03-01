package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.handler;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorHandlerDecoratorFactory implements DecoratorFactory<ErrorHandlerConfig> {

    @Override
    public String getType() {
        return "ERROR_HANDLER";
    }

    @Override
    public Class<ErrorHandlerConfig> getConfigClass() {
        return ErrorHandlerConfig.class;
    }

    @Override
    public TaskDecorator create(ErrorHandlerConfig config, String nodeId) {
        return new ErrorHandlerInterceptor(config, nodeId);
    }
}
