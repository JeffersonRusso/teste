package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Gerencia a cadeia de execução de interceptores.
 */
@Slf4j
public class InterceptorStack implements Task {

    private final TaskChain headOfChain;

    public InterceptorStack(Task targetTask,
                            List<InterceptorStep> steps,
                            TaskDefinition taskDefinition) {
        
        TaskChain chain = targetTask::execute;

        for (InterceptorStep step : steps.reversed()) {
            chain = createLink(step, taskDefinition, chain);
        }

        this.headOfChain = chain;
    }

    private TaskChain createLink(InterceptorStep step, TaskDefinition def, TaskChain next) {
        return context -> {
            try {
                // O interceptor agora deve retornar o resultado do proceed
                return step.interceptor().intercept(context, next, step.config(), def);
            } catch (Exception e) {
                log.error("Falha no interceptor [{}]: {}", 
                    step.interceptor().getClass().getSimpleName(), e.getMessage());
                throw e;
            }
        };
    }

    @Override
    public Object execute(ExecutionContext context) {
        return headOfChain.proceed(context);
    }
}
