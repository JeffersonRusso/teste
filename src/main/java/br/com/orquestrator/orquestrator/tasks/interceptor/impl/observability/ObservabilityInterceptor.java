package br.com.orquestrator.orquestrator.tasks.interceptor.impl.observability;

import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ObservabilityInterceptor implements TaskDecorator {

    private final String nodeId;

    @Override
    public TaskResult apply(TaskChain next) {
        long start = System.currentTimeMillis();
        try {
            TaskResult result = next.proceed();
            log.debug("Nó [{}] executado em {}ms", nodeId, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("Nó [{}] falhou após {}ms", nodeId, System.currentTimeMillis() - start);
            throw e;
        }
    }
}
