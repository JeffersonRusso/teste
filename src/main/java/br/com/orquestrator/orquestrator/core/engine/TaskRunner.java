package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.listener.TaskExecutionListener;
import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.tasks.base.ContractView;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Executor de tasks que gerencia o isolamento de dados e a integridade do barramento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner implements TaskExecutor {

    private final TaskRegistry taskRegistry;
    private final List<TaskExecutionListener> listeners;

    @Override
    public void execute(final TaskDefinition definition, final ExecutionContext context, final DataBus dataBus) {
        String nodeId = definition.getNodeId().value();

        ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId)
                .run(() -> runWithContext(definition, context, dataBus));
    }

    private void runWithContext(TaskDefinition definition, ExecutionContext context, DataBus dataBus) {
        notifyStart(definition, context);

        try {
            // 1. Sincronização (Interruptível)
            dataBus.waitForDependencies(definition);
            
            // 2. Execução
            TaskData taskData = new ContractView(context, definition);
            Task task = taskRegistry.getTask(definition);
            task.execute(taskData);
            
            // 3. Finalização
            markSuccess(definition, context);
            dataBus.publishResults(definition, context);
            
            notifySuccess(definition, context);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            dataBus.failResults(definition); // Desbloqueia dependentes
            notifyError(definition, context, e);
        } catch (Exception e) {
            dataBus.failResults(definition); // Desbloqueia dependentes
            notifyError(definition, context, e);
            handleFailure(definition, dataBus, e);
        }
    }

    private void markSuccess(TaskDefinition definition, ExecutionContext context) {
        String nodeId = definition.getNodeId().value();
        if (TaskMetadataHelper.get(context, nodeId, TaskMetadataHelper.STATUS) == null) {
            TaskMetadataHelper.update(context, nodeId, TaskMetadataHelper.STATUS, 200);
        }
    }

    private void handleFailure(final TaskDefinition definition, final DataBus dataBus, final Exception e) {
        if (definition.isFailFast()) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
        log.warn("Task [{}] falhou (failFast=false). Erro: {}", definition.getNodeId(), e.getMessage());
    }

    private void notifyStart(TaskDefinition def, ExecutionContext ctx) {
        listeners.forEach(l -> l.onStart(def, ctx));
    }

    private void notifySuccess(TaskDefinition def, ExecutionContext ctx) {
        listeners.forEach(l -> l.onSuccess(def, ctx));
    }

    private void notifyError(TaskDefinition def, ExecutionContext ctx, Exception e) {
        listeners.forEach(l -> l.onError(def, ctx, e));
    }
}
