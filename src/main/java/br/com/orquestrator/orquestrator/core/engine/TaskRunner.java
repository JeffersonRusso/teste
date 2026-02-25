package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.result.TaskResultProcessor;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TaskRunner: Orquestrador de execução de unidade de trabalho.
 * Otimizado para reduzir contenção de logs e alocações em cenários de alta carga.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner {

    private final TaskRegistry taskRegistry;
    private final List<TaskResultProcessor> processors;

    public void run(TaskDefinition definition, ExecutionContext context) {
        Task task = taskRegistry.getTask(definition);
        run(task, definition, context);
    }

    /**
     * Versão otimizada para o motor DAG: Recebe a task e definição já resolvidas.
     */
    public void run(Task task, TaskDefinition definition, ExecutionContext context) {
        final String nodeId = definition.getNodeId().value();

        ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId).run(() -> {
            try {
                TaskResult result = task.execute(context);
                
                if (result != null) {
                    // Otimização: Loop for tradicional evita a criação de Iterator
                    for (int i = 0; i < processors.size(); i++) {
                        processors.get(i).process(result, definition, context);
                    }
                }
            } catch (Exception e) {
                // REDUÇÃO DE CONTENÇÃO: Só logamos o erro se o nível de log permitir, 
                // e evitamos concatenar strings se não for necessário.
                if (log.isErrorEnabled()) {
                    log.error("Falha no nó [{}]: {}", nodeId, e.getMessage());
                }
                
                context.setError(nodeId, e.getMessage());

                if (definition.isFailFast()) {
                    throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
                }
            }
        });
    }
}
