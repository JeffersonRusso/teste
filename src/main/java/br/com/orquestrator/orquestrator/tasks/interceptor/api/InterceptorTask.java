package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InterceptorTask implements Task {

    private final Task coreTask;
    private final List<TaskInterceptor> interceptors;

    @Override
    public TaskResult execute(TaskContext context) {
        TaskInterceptor.Chain chain = new RealInterceptorChain(interceptors, 0, context, coreTask);
        return chain.proceed(context);
    }
}
