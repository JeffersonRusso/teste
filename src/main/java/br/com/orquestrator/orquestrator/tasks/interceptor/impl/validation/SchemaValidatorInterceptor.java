package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

/**
 * SchemaValidatorInterceptor: Valida o esquema dos dados no contexto.
 * Usa apenas a visão de leitura (Privilégio Mínimo).
 */
@RequiredArgsConstructor
public class SchemaValidatorInterceptor implements TaskDecorator {

    @Override
    public TaskResult apply(TaskChain next) {
        // Usa a visão de leitura do escopo
        ReadableContext reader = ContextHolder.reader();
        
        // Lógica de validação de schema aqui...
        
        return next.proceed();
    }
}
