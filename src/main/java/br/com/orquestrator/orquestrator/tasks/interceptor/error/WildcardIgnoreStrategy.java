package br.com.orquestrator.orquestrator.tasks.interceptor.error;

import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import org.springframework.stereotype.Component;

@Component
public class WildcardIgnoreStrategy implements ErrorIgnoreStrategy {
    @Override
    public boolean shouldIgnore(Throwable e, ErrorHandlerConfig config) {
        return config.ignoreExceptions().contains("*") || config.ignoreNodes().contains("*");
    }
}
