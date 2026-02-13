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

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRunner implements TaskExecutor {

    private final TaskRegistry taskRegistry;
    // Injetamos o Composite (ou a lista se preferir iterar aqui, mas o Composite é mais limpo)
    private final TaskExecutionListener executionListener;

    @Override
    public void execute(final TaskDefinition definition, final ExecutionContext context, final DataBus dataBus) {
        String nodeId = definition.getNodeId().value();

        // Java 21: Propagação de contexto imutável e eficiente
        ScopedValue.where(ContextHolder.CURRENT_NODE, nodeId)
                .run(() -> runWithContext(definition, context, dataBus));
    }

    private void runWithContext(TaskDefinition definition, ExecutionContext context, DataBus dataBus) {
        // 1. Início do Rastro (Delegado)
        executionListener.onStart(definition, context);

        try {
            // 2. Barreira de Sincronização (Interruptível para StructuredTaskScope)
            dataBus.waitForDependencies(definition);
            
            // 3. Preparação e Localização
            TaskData taskData = new ContractView(context, definition);
            Task task = taskRegistry.getTask(definition);
            
            // 4. Execução (Pode ser a Task pura ou a InterceptorStack)
            task.execute(taskData);
            
            // 5. Sucesso: Metadados, Barramento e Notificação
            updateSuccessMetadata(definition, context);
            dataBus.publishResults(definition, context);
            executionListener.onSuccess(definition, context);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            handleTaskError(definition, context, dataBus, e);
        } catch (Exception e) {
            handleTaskError(definition, context, dataBus, e);
            
            // Lógica de Fail-Fast: O motor decide se o pipeline morre
            if (definition.isFailFast()) {
                throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
            }
        }
    }

    private void handleTaskError(TaskDefinition def, ExecutionContext ctx, DataBus bus, Exception e) {
        bus.failResults(def); // Desbloqueia dependentes no Barramento
        executionListener.onError(def, ctx, e); // Notifica erro com segurança
        log.error("Falha na execução do nó [{}]: {}", def.getNodeId(), e.getMessage());
    }

    private void updateSuccessMetadata(TaskDefinition definition, ExecutionContext context) {
        String nodeId = definition.getNodeId().value();
        // Garante rastro de status 200 se a task não o fez explicitamente
        if (TaskMetadataHelper.get(context, nodeId, TaskMetadataHelper.STATUS) == null) {
            TaskMetadataHelper.update(context, nodeId, TaskMetadataHelper.STATUS, 200);
        }
    }
}
