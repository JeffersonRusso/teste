package br.com.orquestrator.orquestrator.core.pipeline.compiler;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;

import java.util.List;

/**
 * TaskChainAssembler: Compila interceptores e tasks em uma única unidade de execução.
 * 
 * Design "State-of-the-Art":
 * - Transforma uma lista de interceptores + task em uma única Task (Boneca Russa).
 * - Isso evita alocações de objetos de controle (Chain, Context) durante a execução real.
 * - Mantém o design de Middleware mas com performance de chamada direta.
 */
public final class TaskChainAssembler {

    private TaskChainAssembler() {}

    /**
     * Compila uma lista de interceptores e uma task core em uma única Task executável.
     */
    public static Task assemble(Task core, List<TaskInterceptor> interceptors) {
        if (interceptors == null || interceptors.isEmpty()) {
            return core;
        }

        // Começamos do último elo (a Task core) e vamos "envelopando" com os interceptores
        // de trás para frente. Isso garante que o primeiro interceptor seja o primeiro a rodar.
        Task compiled = core;
        
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            final TaskInterceptor interceptor = interceptors.get(i);
            final Task nextTask = compiled;
            
            // Envelopamos a execução como uma nova Task
            compiled = (context) -> {
                TaskChain chain = new TaskChain() {
                    @Override
                    public TaskResult proceed(TaskExecutionContext ctx) {
                        return nextTask.execute(ctx);
                    }
                };
                return interceptor.intercept(context, chain);
            };
        }
        
        return compiled;
    }
}
