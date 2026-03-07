package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.handler;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ErrorHandlerInterceptor implements TaskInterceptor {

    private final ErrorHandlerConfig config;
    private final String nodeId;

    @Override
    public TaskResult intercept(Chain chain) {
        try {
            return chain.proceed(chain.context());
        } catch (Exception e) {
            log.error("Erro capturado no nó [{}]: {}", nodeId, e.getMessage());
            ContextHolder.writer().put(nodeId + ".error", DataValue.of(e.getMessage()));

            if ("IGNORE".equalsIgnoreCase(config.action())) {
                log.warn("Ignorando erro no nó [{}] conforme configuração.", nodeId);
                return TaskResult.success(DataValue.of(config.fallbackValue()));
            }
            
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
