package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.fallback;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.FallbackConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FallbackInterceptor: Retorna um valor padrão em caso de falha na tarefa.
 */
@Slf4j
@RequiredArgsConstructor
public class FallbackInterceptor implements TaskInterceptor {

    private final FallbackConfig config;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        try {
            return chain.proceed(chain.inputs());
        } catch (Exception e) {
            log.warn("Acionando Fallback para o nó [{}]: {}", nodeId, e.getMessage());
            return TaskResult.success(config.value());
        }
    }
}
