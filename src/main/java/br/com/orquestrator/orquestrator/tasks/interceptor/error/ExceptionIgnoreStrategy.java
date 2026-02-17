package br.com.orquestrator.orquestrator.tasks.interceptor.error;

import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import org.springframework.stereotype.Component;

@Component
public class ExceptionIgnoreStrategy implements ErrorIgnoreStrategy {
    @Override
    public boolean shouldIgnore(Throwable e, ErrorHandlerConfig config) {
        String exName = e.getClass().getName();
        String causeName = (e.getCause() != null) ? e.getCause().getClass().getName() : "";
        return config.ignoreExceptions().contains(exName) || config.ignoreExceptions().contains(causeName);
    }
}
