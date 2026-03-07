package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class InputDecorator implements TaskInterceptor {

    private final Function<ReadableContext, Map<String, Object>> inputResolver;
    private final Set<String> requiredFields;

    @Override
    public TaskResult intercept(Chain chain) {
        Map<String, Object> inputs = inputResolver.apply(ContextHolder.reader());
        
        TaskContext enrichedContext = new TaskContext(
            inputs, 
            chain.context().configuration(), 
            chain.context().nodeId(), 
            requiredFields
        );
        
        try {
            return ScopedValue.where(ContextHolder.CURRENT_INPUTS, inputs)
                    .call(() -> chain.proceed(enrichedContext));
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
