package br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryInterceptorProvider implements InterceptorProvider<RetryConfig> {

    private final br.com.orquestrator.orquestrator.tasks.interceptor.impl.resilience.retry.RetryInterceptor interceptor;

    @Override
    public String featureType() {
        return "retry";
    }

    @Override
    public Class<RetryConfig> configClass() {
        return RetryConfig.class;
    }

    @Override
    public TaskInterceptor create(RetryConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
