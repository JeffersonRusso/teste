package br.com.orquestrator.orquestrator.tasks.interceptor.impl.logging;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.LogResponseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LogResponseInterceptor: Registra o resultado da tarefa no log.
 */
@Slf4j
@RequiredArgsConstructor
public class LogResponseInterceptor implements TaskInterceptor {

    private final LogResponseConfig config;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        TaskResult result = chain.proceed(chain.inputs());
        
        if (log.isInfoEnabled()) {
            log.info("Nó [{}] retornou status: {} | Body: {}", nodeId, result.status(), result.body());
        }
        
        return result;
    }
}
