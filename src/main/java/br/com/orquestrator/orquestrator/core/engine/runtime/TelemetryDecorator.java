package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * TelemetryDecorator: Captura métricas de execução.
 */
@RequiredArgsConstructor
public class TelemetryDecorator implements TaskInterceptor {
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        return chain.proceed(chain.inputs());
    }
}
