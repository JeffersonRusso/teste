package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.tasks.interceptor.config.RetryConfig;
import br.com.orquestrator.orquestrator.tasks.interceptor.resilience.ResilienceInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.resilience.RetryStrategy;
import org.springframework.stereotype.Component;

/**
 * RetryInterceptor: Agora apenas uma casca para a RetryStrategy.
 */
@Component("RETRY")
public class RetryInterceptor extends ResilienceInterceptor<RetryConfig> {
    public RetryInterceptor(RetryStrategy strategy) {
        super(RetryConfig.class, strategy);
    }
}
