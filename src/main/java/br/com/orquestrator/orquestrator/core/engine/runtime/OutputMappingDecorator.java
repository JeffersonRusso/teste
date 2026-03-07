package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class OutputMappingDecorator implements TaskInterceptor {

    private final BiConsumer<TaskResult, WriteableContext> outputMapper;

    @Override
    public TaskResult intercept(Chain chain) {
        TaskResult result = chain.proceed(chain.context());
        
        if (result.isSuccess()) {
            outputMapper.accept(result, ContextHolder.writer());
        }
        
        return result;
    }
}
