package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import br.com.orquestrator.orquestrator.tasks.registry.factory.TaskChainCompiler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * BackgroundExecutionEngine: Executa tarefas em segundo plano.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundExecutionEngine {

    private final TaskRegistry taskRegistry;
    private final TaskChainCompiler chainCompiler;
    private final IdGenerator idGenerator;

    public void execute(TaskDefinition def) {
        RequestIdentity identity = new RequestIdentity(
            idGenerator.generateFastId(),
            "BACKGROUND_" + def.nodeId().value(),
            "ORDER_BG",
            idGenerator.generateFastId(),
            Collections.emptySet()
        );

        Task coreTask = taskRegistry.getTask(def);
        Task executable = chainCompiler.compile(coreTask, def);

        try {
            // Executa a tarefa com inputs vazios
            executable.execute(Collections.emptyMap());
        } catch (Exception e) {
            log.error("Falha na task background [{}]: {}", def.nodeId().value(), e.getMessage());
        }
    }
}
