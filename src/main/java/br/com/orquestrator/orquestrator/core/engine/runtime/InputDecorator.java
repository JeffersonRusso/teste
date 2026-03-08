package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * InputDecorator: Garante que os campos obrigatórios (requiredFields) 
 * sejam propagados para a execução da tarefa.
 * Agora desacoplado do ContextHolder e focado no Shadow Context.
 */
@RequiredArgsConstructor
public class InputDecorator implements TaskInterceptor {

    private final Set<String> requiredFields;

    @Override
    public TaskResult intercept(Chain chain) {
        // O context() já contém os inputs coletados pelo DefaultExecutionNode (Shadow Context)
        TaskContext enrichedContext = new TaskContext(
            chain.context().inputs(), 
            chain.context().configuration(), 
            chain.context().nodeId(), 
            requiredFields
        );
        
        return chain.proceed(enrichedContext);
    }
}
