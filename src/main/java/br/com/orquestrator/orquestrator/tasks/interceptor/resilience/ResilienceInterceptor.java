package br.com.orquestrator.orquestrator.tasks.interceptor.resilience;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.TypedTaskInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * ResilienceInterceptor: Base para interceptores que usam ResilienceStrategy.
 */
@Slf4j
public abstract class ResilienceInterceptor<C> extends TypedTaskInterceptor<C> {

    private final ResilienceStrategy<C> strategy;

    protected ResilienceInterceptor(Class<C> configClass, ResilienceStrategy<C> strategy) {
        super(configClass);
        this.strategy = strategy;
    }

    @Override
    protected TaskResult interceptTyped(ExecutionContext context, TaskChain next, C config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        return strategy.execute(() -> next.proceed(context), nodeId, config);
    }
}
