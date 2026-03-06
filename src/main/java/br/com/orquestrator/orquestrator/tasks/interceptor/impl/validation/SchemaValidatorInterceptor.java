package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchemaValidatorInterceptor implements TaskDecorator {

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        ReadableContext reader = ContextHolder.reader();
        
        // Lógica de validação de schema aqui...
        
        return next.proceed(context);
    }
}
