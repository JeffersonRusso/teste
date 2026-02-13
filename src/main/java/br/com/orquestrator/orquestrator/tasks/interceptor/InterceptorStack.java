package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;

import java.util.List;

/**
 * Gerencia a cadeia de execução de interceptores.
 * Constrói uma pilha de chamadas onde cada interceptor envolve o próximo.
 */
public class InterceptorStack implements Task {

    private final TaskChain headOfChain;

    public InterceptorStack(Task targetTask,
                            List<InterceptorStep> steps,
                            TaskDefinition taskDefinition) {
        
        // O elo final da cadeia é a execução da task real
        TaskChain chain = targetTask::execute;

        // Constrói a pilha de trás para frente
        for (InterceptorStep step : steps.reversed()) {
            chain = createLink(step, taskDefinition, chain);
        }

        this.headOfChain = chain;
    }

    private TaskChain createLink(InterceptorStep step, TaskDefinition def, TaskChain next) {
        // Nota: Os interceptores ainda precisam ser migrados para TaskData se necessário,
        // mas por enquanto mantemos a compatibilidade se eles usarem o context original.
        // Se o interceptor precisar do ExecutionContext, ele pode fazer o cast do TaskData (se for ContractView).
        return data -> step.interceptor().intercept(data, next, step.config(), def);
    }

    @Override
    public void execute(TaskData data) {
        headOfChain.proceed(data);
    }
}
