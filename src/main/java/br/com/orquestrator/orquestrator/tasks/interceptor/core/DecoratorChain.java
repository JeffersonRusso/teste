package br.com.orquestrator.orquestrator.tasks.interceptor.core;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * DecoratorChain: Executa a cadeia de decoradores de forma síncrona.
 */
@Slf4j
public class DecoratorChain implements Task {

    private final TaskChain executionChain;
    private final String nodeId;

    public DecoratorChain(final Task coreTask, final List<TaskInterceptor> decorators, final TaskDefinition taskDefinition) {
        this.nodeId = taskDefinition.nodeId().value();
        
        // Constrói a cadeia de execução uma única vez
        TaskChain chain = coreTask::execute;
        for (int i = decorators.size() - 1; i >= 0; i--) {
            chain = decorators.get(i).apply(chain);
        }
        this.executionChain = chain;
    }

    @Override
    public TaskResult execute() {
        try {
            return executionChain.proceed();
        } catch (Exception e) {
            throw new RuntimeException("Falha na execução da task " + nodeId, e);
        }
    }
}
