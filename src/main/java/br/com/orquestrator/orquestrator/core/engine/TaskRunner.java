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
 * Segue o princípio de Open/Closed: novos comportamentos de resultado 
 * são adicionados via TaskResultProcessor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner {

    private final TaskRegistry taskRegistry;
    private final List<TaskResultProcessor> processors;

    public void run(TaskDefinition definition, ExecutionContext context) {
        String nodeId = definition.getNodeId().value();

        ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId).run(() -> {
            try {
                Task task = taskRegistry.getTask(definition);
                TaskResult result = task.execute(context);
                
                if (result != null) {
                    processors.forEach(p -> p.process(result, definition, context));
                }
            } catch (Exception e) {
                log.error("Falha no nó [{}]: {}", nodeId, e.getMessage());
                context.setError(nodeId, e.getMessage());
                if (definition.isFailFast()) {
                    throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
                }
            }
        });
    }
}
