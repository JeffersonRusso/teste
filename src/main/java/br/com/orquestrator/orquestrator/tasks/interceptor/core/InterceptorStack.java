package br.com.orquestrator.orquestrator.tasks.interceptor.core;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * InterceptorStack: Executa a cadeia de interceptores de forma síncrona.
 * Otimizado: Não cria Virtual Threads internas, pois o orquestrador já gerencia o paralelismo.
 */
@Slf4j
public class InterceptorStack implements Task {

    private final TaskChain executionChain;
    private final String nodeId;

    public InterceptorStack(final Task coreTask, final List<TaskInterceptor> interceptors, final TaskDefinition taskDefinition) {
        this.nodeId = taskDefinition.getNodeId().value();
        
        // Constrói a cadeia de execução uma única vez (na criação da task)
        TaskChain chain = coreTask::execute;
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            chain = interceptors.get(i).apply(chain);
        }
        this.executionChain = chain;
    }

    @Override
    public TaskResult execute(final ExecutionContext context) {
        try {
            // Execução direta na thread que chamou (já é uma Virtual Thread do orquestrador)
            return executionChain.proceed(context);
        } catch (Exception e) {
            throw new InterceptorExecutionException("Falha na execução da task " + nodeId, e);
        }
    }
}
