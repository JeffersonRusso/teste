package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * RealInterceptorChain: Implementação da cadeia de execução de interceptores.
 */
public class RealInterceptorChain implements TaskInterceptor.Chain {

    private final List<TaskInterceptor> interceptors;
    private final int index;
    private final Map<String, JsonNode> inputs;
    private final Task coreTask;

    public RealInterceptorChain(List<TaskInterceptor> interceptors, int index, Map<String, JsonNode> inputs, Task coreTask) {
        this.interceptors = interceptors;
        this.index = index;
        this.inputs = inputs;
        this.coreTask = coreTask;
    }

    @Override public Map<String, JsonNode> inputs() { return inputs; }

    @Override
    public TaskResult proceed(Map<String, JsonNode> nextInputs) {
        if (index < interceptors.size()) {
            TaskInterceptor.Chain nextChain = new RealInterceptorChain(interceptors, index + 1, nextInputs, coreTask);
            return interceptors.get(index).intercept(nextChain);
        }
        return coreTask.execute(nextInputs);
    }
}
