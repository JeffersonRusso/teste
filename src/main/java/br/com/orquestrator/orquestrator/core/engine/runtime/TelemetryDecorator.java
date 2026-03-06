package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TelemetryDecorator implements TaskDecorator {

    private final String nodeId;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        long start = System.currentTimeMillis();
        try {
            TaskResult result = next.proceed(context);
            long duration = System.currentTimeMillis() - start;
            log.debug("Nó [{}] finalizado em {}ms | Sucesso: {}", nodeId, duration, result.isSuccess());
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("Nó [{}] falhou em {}ms | Erro: {}", nodeId, duration, e.getMessage());
            throw e;
        }
    }
}
