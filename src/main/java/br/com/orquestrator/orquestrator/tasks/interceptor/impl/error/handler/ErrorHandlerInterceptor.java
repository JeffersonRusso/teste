package br.com.orquestrator.orquestrator.tasks.interceptor.impl.error.handler;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ErrorHandlerInterceptor implements TaskDecorator {

    private final ErrorHandlerConfig config;
    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        try {
            return next.proceed(context);
        } catch (Exception e) {
            log.error("Erro capturado no nó [{}]: {}", nodeId, e.getMessage());
            
            // Corrigido: Usando DataValue.of
            ContextHolder.writer().put(nodeId + ".error", DataValue.of(e.getMessage()));

            if ("IGNORE".equalsIgnoreCase(config.action())) {
                log.warn("Ignorando erro no nó [{}] conforme configuração.", nodeId);
                return TaskResult.success(DataValue.of(config.fallbackValue()));
            }
            
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
