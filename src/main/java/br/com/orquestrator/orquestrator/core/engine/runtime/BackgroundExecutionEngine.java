package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextFactory;
import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.context.ExecutionContext;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundExecutionEngine {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler chainCompiler;
    private final ContextFactory contextFactory;
    private final IdGenerator idGenerator;

    public void execute(TaskDefinition def) {
        RequestIdentity identity = new RequestIdentity(
            idGenerator.generateFastId(),
            "BACKGROUND_" + def.nodeId().value(),
            "ORDER_BG",
            idGenerator.generateFastId()
        );

        ExecutionContext context = contextFactory.create(identity, Map.of(), Map.of());
        Task coreTask = taskRegistry.getTask(def);
        Task executable = chainCompiler.compile(coreTask, def);

        ScopedValue.where(ContextHolder.CONTEXT, context).run(() -> {
            try {
                // Corrigido: Adicionado Set.of() para requiredFields
                TaskContext taskContext = new TaskContext(Map.of(), null, def.nodeId().value(), Set.of());
                executable.execute(taskContext);
            } catch (Exception e) {
                log.error("Falha na task background [{}]: {}", def.nodeId().value(), e.getMessage());
            }
        });
    }
}
