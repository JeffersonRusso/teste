package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;

import java.util.List;

public class RealInterceptorChain implements TaskInterceptor.Chain {

    private final List<TaskInterceptor> interceptors;
    private final int index;
    private final TaskContext context;
    private final Task coreTask;

    public RealInterceptorChain(List<TaskInterceptor> interceptors, int index, TaskContext context, Task coreTask) {
        this.interceptors = interceptors;
        this.index = index;
        this.context = context;
        this.coreTask = coreTask;
    }

    @Override public TaskContext context() { return context; }

    @Override
    public TaskResult proceed(TaskContext nextContext) {
        if (index < interceptors.size()) {
            // Chama o próximo interceptor da lista
            TaskInterceptor.Chain nextChain = new RealInterceptorChain(interceptors, index + 1, nextContext, coreTask);
            return interceptors.get(index).intercept(nextChain);
        }
        
        // Fim da linha: Executa a task real
        return coreTask.execute(nextContext);
    }
}
