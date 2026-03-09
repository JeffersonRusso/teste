package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * InterceptorTask: Envolve uma tarefa em uma cadeia de interceptores.
 */
@RequiredArgsConstructor
public class InterceptorTask implements Task {

    private final Task coreTask;
    private final List<TaskInterceptor> interceptors;

    @Override
    public TaskResult execute(Map<String, JsonNode> inputs) {
        TaskInterceptor.Chain chain = new RealInterceptorChain(interceptors, 0, inputs, coreTask);
        return chain.proceed(inputs);
    }
}
