package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * TelemetryDecorator: Otimizado para alta performance.
 * Removemos o log síncrono que causava contenção massiva.
 */
@RequiredArgsConstructor
public class TelemetryDecorator implements TaskInterceptor {
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        // OTIMIZAÇÃO: Em produção, use Micrometer/Prometheus aqui.
        // Removido log.debug para eliminar context switching.
        return chain.proceed(chain.context());
    }
}
