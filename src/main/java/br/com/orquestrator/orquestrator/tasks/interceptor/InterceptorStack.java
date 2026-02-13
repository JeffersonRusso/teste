package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Gerencia a cadeia de execução de interceptores (Features).
 * Aplica o padrão Decorator para envolver a Task real com comportamentos transversais.
 * Utiliza recursos do Java 21 para garantir imutabilidade e clareza na construção da pilha.
 */
@Slf4j
public class InterceptorStack implements Task {

    private final TaskChain headOfChain;

    public InterceptorStack(Task targetTask,
                            List<InterceptorStep> steps,
                            TaskDefinition taskDefinition) {
        
        // 1. O elo final da corrente é sempre a execução da Task core (ex: HttpTask, GroovyTask)
        TaskChain chain = targetTask::execute;

        // 2. Construímos a pilha de trás para frente (LIFO)
        // Java 21: Uso de reversed() da SequencedCollection para iterar de forma fluida.
        // Os interceptores que aparecem primeiro na lista original serão os "mais externos".
        for (InterceptorStep step : steps.reversed()) {
            chain = createLink(step, taskDefinition, chain);
        }

        this.headOfChain = chain;
    }

    private TaskChain createLink(InterceptorStep step, TaskDefinition def, TaskChain next) {
        // Cada elo da corrente recebe o próximo a ser executado
        return data -> {
            try {
                step.interceptor().intercept(data, next, step.config(), def);
            } catch (Exception e) {
                log.error("Falha crítica no interceptor [{}]: {}", 
                    step.interceptor().getClass().getSimpleName(), e.getMessage());
                throw e; // Propaga para que o TaskRunner decida o Fail-Fast
            }
        };
    }

    @Override
    public void execute(TaskData data) {
        // Dispara a execução do primeiro interceptor da pilha (o mais externo)
        headOfChain.proceed(data);
    }
}
